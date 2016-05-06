package com.ly.entity;

import java.io.Serializable;

/**
 * 用户信息实体类
 *
 * @author ly
 */
public class User implements Serializable {
    String id;
    String userName;
    String userPwd;
    String nickName;
    String sex;
    String birthday;
    String sign;
    String portrait;

    public User() {
        super();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sign='" + sign + '\'' +
                ", portrait='" + portrait + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public User(String id, String userName, String userPwd, String nickName, String sex, String birthday, String sign, String portrait) {
        this.id = id;
        this.userName = userName;
        this.userPwd = userPwd;
        this.nickName = nickName;
        this.sex = sex;
        this.birthday = birthday;
        this.sign = sign;
        this.portrait = portrait;
    }
}
