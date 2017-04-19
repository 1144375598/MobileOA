package com.chenxujie.mobileoa.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;



public class User extends BmobUser {
    private Boolean sex;  //true为男，false为女
    private Integer age;
    private String department;
    private String position;
    private String address;
    private BmobDate hiredate;
    private String education;
    private String name;
    private String photoUrl;
    private String photoName;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public BmobDate getHiredate() {
        return hiredate;
    }

    public void setHiredate(BmobDate hiredate) {
        this.hiredate = hiredate;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
