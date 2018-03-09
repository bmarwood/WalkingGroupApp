package com.teal.a276.walkinggroup;


import com.teal.a276.walkinggroup.dataobjects.User;

import org.junit.Test;
import org.junit.Assert.*;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Brian on 3/9/2018.
 */

public class UserTest {
    private User user = new User();
    @Test
    public void setAndGetName() throws Exception{
        user.setName("bill");
        assertEquals("bill",user.getName());
    }
    @Test
    public void getAndSetId() throws Exception {
        Long l =(long) 10;
        user.setId(l);
        assertEquals(l,user.getId());
    }
    @Test
    public void getAndSetEmail() throws Exception{
        user.setEmail("asdf@test.com");
        assertEquals("asdf@test.com", user.getEmail());
    }
    @Test
    public void getAndSetPassword() throws Exception{
        user.setPassword("1234");
        assertEquals("1234", user.getPassword());
    }


}
