package com.git.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.git.blog.dao.mapper")
public class ProxyApplication {

	public static void main(String[] args) {
	    //configurableApplicationContext其实就是在ApplicationContext的基础上增加了生命周期管理的功能
        ConfigurableApplicationContext context=SpringApplication.run(ProxyApplication.class, args);
        System.out.println("---will stop---");
        System.out.println("id->"+context.getId());
        System.out.println("application->"+context.getApplicationName());
        System.out.println("displayName->"+context.getDisplayName());
        System.out.println("bean count->"+context.getBeanDefinitionCount());
        //spring容器中所有的bean
        /*String[] beans=context.getBeanDefinitionNames();
        for(String s:beans){
            System.out.println(s);
        }*/
        //关闭系统
        //context.close();
    }

}
