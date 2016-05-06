package com.ly.utils;

import android.os.Environment;

/**
 * 常量类
 */
public class Constants {

    /**
     * 服务器根地址
     */
    public static final String SEVER_PATH="http://10.7.155.79:8080/Read";
    /**
     * 图片地址
     */
    public static final String IMG_PATH =SEVER_PATH+"/upload/image";
    /**
     * 头像根地址
     */
    public static final String PORTRAIT_PATH=SEVER_PATH+"/upload/portrait";
    /**
     * 修改签名的地址
     */
    public static final String MODIFY_SIG=SEVER_PATH+"/modifySig.do";
    /**
     * 修改性别的地址
     */
    public static final String MODIFY_SEX=SEVER_PATH+"/modifySex.do";
    /**
     * 修改昵称的地址
     */
    public static final String MODIFY_NAME=SEVER_PATH+"/modifynickName.do";
    /**
     * 图片缓存地址
     */
    public static final String CACHE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 首页地址
     */
    public static final String HOMEPAGE_PATH=SEVER_PATH+"/homePage.do";
    /**
     * 我发布的 地址
     */
    public static final String MYSEND_PATH = SEVER_PATH+"/mySend.do";
    /**
     * 我喜欢的 地址
     */
    public static final String MYLIKE_PATH = SEVER_PATH+"/myLike.do";
    /**
     *登录地址
     */
    public static final String LOGIN_PATH = SEVER_PATH+"/login.do";
    /**
     *第三方登录 地址
     */
    public static final String THIRD_LOGIN_PATH = SEVER_PATH+"/thirdlogin.do";
    /**
     *注册地址
     */
    public static final String REGIST_PATH = SEVER_PATH+"/register.do";
    /**
     *修改密码的地址
     */
    public static final String UPDATAPWD_PATH = SEVER_PATH+"/password.do";


}
