package com.git.blog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.git.blog.exception.BusinessException;
import com.git.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangke18 on 2018/6/12.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/allUsers")
    public String getAllUsers(){
        long start=System.currentTimeMillis();
        try{
            return JSON.toJSONString(userService.getAllUsers(), SerializerFeature.WriteDateUseDateFormat);
        }finally {
            System.out.println("cost time->"+(System.currentTimeMillis()-start)+"ms");
        }
    }

    @RequestMapping("/delete")
    public int deleteById(@RequestParam Long id){
        return userService.deleteById(id);
    }

    @RequestMapping("/exists")
    public boolean exists(@RequestParam String user){
        return userService.exists(user);
    }

    @RequestMapping("/login")
    public String login(@RequestParam String user, @RequestParam String pswd){
        long start=System.currentTimeMillis();
        try{
            switch (userService.login(user, pswd)){
                case -1: return "您的账户已被冻结";
                case 0: return "账户不存在或密码错误";
                case 1: return "登录成功";
                default: return "未知错误";
            }
        }finally {
            System.out.println("cost time->"+(System.currentTimeMillis()-start)+"ms");
        }

    }

    @RequestMapping("/updatePassword")
    public int changePassword(@RequestParam Long id, @RequestParam String user, @RequestParam String pswd){
        try {
            return userService.changePassword(id, user, pswd);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
