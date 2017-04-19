package com.chenxujie.mobileoa.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by minxing on 2017-04-19.
 */

public class Email extends BmobObject {
    private String title;
    private String content;
    private String sender;
    private String receiver;
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
