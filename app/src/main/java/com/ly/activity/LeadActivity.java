package com.ly.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.ly.entity.Book;
import com.ly.entity.User;
import com.ly.happyreader.MainActivity;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;

import java.util.List;

public class LeadActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lead);
        initSharedPreference();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LeadActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);
        init();
    }

    /**
     * 加载书籍数据
     */
    private void init() {
        DbUtils dbUtils = DbUtils.create(this);
        try {
            List<Book> all = dbUtils.findAll(Book.class);
            if (all != null) {//如果数据库中数据不为0
                ((ReadApplication) (getApplication())).setBookList(all);
            }
        } catch (DbException e) {

        }
    }

    /**
     * 默认加载登录数据
     */
    private void initSharedPreference() {
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        User user = new User();
        user.setId(sharedPreferences.getString("id", null));
        user.setUserName(sharedPreferences.getString("userName", null));
        user.setUserPwd(sharedPreferences.getString("userPwd", null));

        user.setBirthday(sharedPreferences.getString("birthday", null));
        user.setNickName(sharedPreferences.getString("nickName", null));
        user.setPortrait(sharedPreferences.getString("portrait", null));
        user.setSex(sharedPreferences.getString("sex", null));
        user.setSign(sharedPreferences.getString("sign", null));
        //如果用户名不为空，代表已经缓存了用户，进行用户全局加载
        if (!TextUtils.isEmpty(user.getNickName())) {
            ((ReadApplication) getApplication()).setUser(user);
            Log.i("-----------------", user.toString());
        }
    }
}
