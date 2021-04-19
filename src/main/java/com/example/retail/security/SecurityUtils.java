package com.example.retail.security;

import com.example.retail.domain.User;

//@Component
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SecurityUtils {

    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user_) {
        user = user_;
    }

}
