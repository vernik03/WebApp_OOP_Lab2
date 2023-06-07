package com.vernik03.payment.security;

import com.vernik03.payment.model.User;
import org.springframework.stereotype.Component;

@Component
public class LogIn {
    private User loginedUser;

    public void setLoginedUser(User user){
        loginedUser = user;
    }

    public User getLoginedUser(){
        return loginedUser;
    }
}
