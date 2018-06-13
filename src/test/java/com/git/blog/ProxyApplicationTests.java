package com.git.blog;

import com.git.blog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProxyApplicationTests {

    @Autowired
    private UserService userService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void test(){
	    if(userService.exists("wonking")){
            System.out.println(true);
        }
    }

}
