package com.git.blog.exception;

import com.git.blog.enums.ErrorEnum;

/**
 * Created by wangke18 on 2018/6/13.
 */
public class BusinessException extends Exception{
    private int code;
    private String message;

    public BusinessException(ErrorEnum error){
        //这里需要调用父构造器，把错误描述传进去，这样异常的冒号后面才会显示内容
        super(error.getMessage());
        code=error.getCode();
        message=error.getMessage();
    }
}
