package com.git.blog.util;

import org.springframework.beans.BeanUtils;

/**
 * Created by wangke18 on 2018/6/13.
 */
public class PropertyUtil {

    @SuppressWarnings("unchecked")
    public static <T> T copyProperties(T t){
        if(t==null){
            return null;
        }
        try {
            T d= (T) t.getClass().newInstance();
            BeanUtils.copyProperties(t, d);
            return d;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
