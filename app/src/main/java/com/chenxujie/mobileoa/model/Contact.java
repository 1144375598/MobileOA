package com.chenxujie.mobileoa.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by minxing on 2017-04-10.
 */

public class Contact extends BmobObject {
    private String telephone;
    private String name;
    private String email;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
