package com.git.blog.enums;

/**
 * Created by wangke18 on 2018/6/13.
 */
public enum ErrorEnum {
    SUCCESS(0, "SUCCESS"),
    UPDATE_FAIL(1000,"update table fail"),
    ILLEGAL_OPERATION(1001,"illegal operation"),
    UNKNOWN_ERROR(9999,"unknown error");

    private int code;
    private String message;

    private ErrorEnum(int c, String m){
        code=c;
        message=m;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
