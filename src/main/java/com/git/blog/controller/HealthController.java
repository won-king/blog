package com.git.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangke18 on 2018/6/8.
 */
@RestController
@RequestMapping("/")
public class HealthController{

    @Value("${value.name}")
    private String name;

    @RequestMapping("/health")
    public String health(HttpServletResponse response){
        return name+" success";
    }

    private void printName(){
        System.out.println("name->"+name);
    }

}
