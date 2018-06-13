package com.git.blog.service;

import com.alibaba.fastjson.JSON;
import com.git.blog.dao.entity.UpdateRecordEntity;
import com.git.blog.dao.entity.UserEntity;
import com.git.blog.dao.mapper.UpdateRecordEntityMapper;
import com.git.blog.dao.mapper.UserEntityMapper;
import com.git.blog.enums.ErrorEnum;
import com.git.blog.exception.BusinessException;
import com.git.blog.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangke18 on 2018/6/12.
 */
@Service
public class UserService {
    //这里之所以提示"Could not autowire. No beans of 'UserEntityMapper' type found"
    //是因为IDEA强大的语法检测功能，是够强大，但是不够智能
    //因为UserEntityMapper是mybatis在运行时生成动态代理时才放入spring容器中的，没有运行，这个bean当然不存在
    //解决方案就是，下载一个mybatis插件即可，网上有个博客讲这个的
    @Autowired
    private UserEntityMapper userDao;

    @Autowired
    private UpdateRecordEntityMapper updateRecordDao;

    public List<UserEntity> getAllUsers(){
        return userDao.selectAll();
    }

    public int deleteById(Long id){
        return userDao.deleteByPrimaryKey(id);
    }

    public UserEntity getById(Long id){
        return userDao.selectByPrimaryKey(id);
    }

    public int saveUser(UserEntity entity){
        return 0;
    }

    public boolean exists(String account){
        return userDao.exists(account);
    }

    public int getUsersNumber(){
        return userDao.countAllUsers();
    }

    public int login(String account, String pswd){
        //首先判断账户是否被冻结，条件是账户存在，并且被冻结
        //这样的话，一个动作要进行两次查询，还不如直接一次把整个user取出来
        //对，没错，一次把整个user取出来，后面判断账户和密码的时候，也不用执行SQL了，这样整个登录过程只需要一个SQL
        /*if(userDao.exists(account) && !userDao.accountEnable(account)){
            return -1;
        }
        return userDao.login(account,pswd) ? 1:0;*/
        UserEntity user=userDao.selectBy(account, pswd);
        //提示用户名不存在或密码错误
        if(user==null){
            return 0;
        }
        //用户名输对了(隐含着并且密码也对的条件)，但是账户被冻结
        if(!user.getEnable()){
            return -1;
        }
        return 1;
    }

    //事务往往是跨表的，所以需要建立一张修改记录表
    // 这里亲测，事务已成功
    // 标志就是，看数据库的记录id，如果updateRecord成功插入一条记录，后面抛出异常，但是这个id还是被用掉了
    // 所以后面插入的新纪录的id就会出现不连续的情况

    //可以愉快的开始探索底层的事务机制了
    //现在先把本职工作干完
    @Transactional(rollbackFor = BusinessException.class)
    public int changePassword(Long id, String account, String pswd) throws BusinessException{
        UserEntity old=userDao.selectByPrimaryKey(id);
        if(old==null || !old.getEnable() || !old.getAccount().equals(account)){
            throw new BusinessException(ErrorEnum.ILLEGAL_OPERATION);
        }
        UserEntity update= PropertyUtil.copyProperties(old);
        update.setPassword(pswd);
        if(userDao.updateByPrimaryKeySelective(update) <1){
            throw new BusinessException(ErrorEnum.UPDATE_FAIL);
        }
        UpdateRecordEntity updateRecord=new UpdateRecordEntity();
        updateRecord.setTableName("user");
        updateRecord.setRecord(JSON.toJSONString(old));
        if(updateRecordDao.insertSelective(updateRecord) <1){
            throw new BusinessException(ErrorEnum.UPDATE_FAIL);
        }
        //触发回滚操作
        if("6666".equals(pswd)){
            throw new BusinessException(ErrorEnum.UPDATE_FAIL);
        }
        return 1;
    }
}
