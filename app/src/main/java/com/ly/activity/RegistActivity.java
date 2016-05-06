package com.ly.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.entity.User;
import com.ly.happyreader.R;
import com.ly.utils.Constants;

public class RegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_username;
    private EditText et_userpass;
    private HttpUtils httpUtils;
    private ImageView img_back;
    private LinearLayout linear_regist;
    private ImageView img_qq;
    private ImageView img_weChat;
    private ImageView img_microblog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        init();

    }

    /**
     * 初始化
     */
    private void init() {
        linear_regist.setOnClickListener(this);
        httpUtils = new HttpUtils(1000);
    }

    /**
     * 找控件
     */
    private void initView() {
        et_username = (EditText) findViewById(R.id.activity_regist_et_username);
        et_userpass = (EditText) findViewById(R.id.activity_regist_et_userpass);
        linear_regist = (LinearLayout) findViewById(R.id.activity_regist_linearlayout_regist);
    }

    /**
     * 注册的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.activity_regist_linearlayout_regist:
                registClick();
                break;
        }
    }

    /**
     * 点击注册按钮
     */
    private void registClick() {
        //获取输入框的内容
        final String name = et_username.getText().toString();
        final String pass = et_userpass.getText().toString();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pass)) {
            Toast.makeText(RegistActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            et_username.requestFocus();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegistActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            et_username.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(RegistActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            et_userpass.requestFocus();
        } else {
            //数据请求
            RequestParams params = new RequestParams("utf-8");
            params.addBodyParameter("userName", name);
            params.addBodyParameter("userPwd", pass);
            httpUtils.send(HttpRequest.HttpMethod.POST, Constants.REGIST_PATH,
                    params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Intent intent = new Intent();
                            intent.putExtra("userName", name);
                            intent.putExtra("userPwd", pass);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(RegistActivity.this, "该用户已存在", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
