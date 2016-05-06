package com.ly.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.entity.User;

import com.ly.happyreader.MainActivity;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;
import com.ly.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_username;
    private EditText et_userpass;
    private HttpUtils httpUtils;
    private TextView tv_rigst;

    private SharedPreferences sharedPreferences;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initview();
        init();
    }

    private void init() {
        btn_login.setOnClickListener(this);
        tv_rigst.setOnClickListener(this);
        httpUtils = new HttpUtils(1000);
    }

    /**
     * 找控件
     */
    private void initview() {
        et_username = (EditText) findViewById(R.id.activity_login_et_username);
        et_userpass = (EditText) findViewById(R.id.activity_login_et_userpass);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_rigst = (TextView) findViewById(R.id.activity_login_tv_rigst);
    }

    /**
     * 登录界面的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                loginClick();
                break;
            case R.id.activity_login_tv_rigst:
                rigstClick();
                break;
        }
    }


    /**
     * 点击登录跳转到首页
     */
    private void loginClick() {
        //获取输入框的内容
        String name = et_username.getText().toString();
        String pass = et_userpass.getText().toString();
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pass)) {
            Toast.makeText(LoginActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            et_username.requestFocus();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            et_username.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            et_userpass.requestFocus();
        } else {
            RequestParams requestParams = new RequestParams("utf-8");
            requestParams.addBodyParameter("userName", name);
            requestParams.addBodyParameter("userPwd", pass);
            httpUtils.send(HttpRequest.HttpMethod.POST, Constants.LOGIN_PATH,
                    requestParams, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            doResult(responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(LoginActivity.this, "密码或账号不正确", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void doResult(String result) {
        try {
            JSONObject object = new JSONObject(result);
            if (object.getString("resultCode").equals("1")) {
                Gson gson = new Gson();
                User user = gson.fromJson(object.getJSONObject("data").toString(), User.class);
                //保存到Application 中去
                saveToSharePreference(user);
                Log.e("info",user.toString());
                //请求成功跳转到主页面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (object.getString("msg").equals("密码错误")) {
                    Toast.makeText(LoginActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveToSharePreference(User user) {
        ((ReadApplication) getApplication()).setUser(user);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("portrait", user.getPortrait());
        edit.putString("id", user.getId());
        edit.putString("birthday", user.getBirthday());
        edit.putString("nickName", user.getNickName());
        edit.putString("sex", user.getSex());
        edit.putString("sign", user.getSign());
        edit.putString("userName", user.getUserName());
        edit.putString("userPwd", user.getUserPwd());
        edit.commit();
    }

    /**
     * 点击跳转到注册页面
     */
    private void rigstClick() {
        Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
        startActivityForResult(intent, 3);

    }

    /**
     * 处理从注册界面带回来的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            Intent intent = getIntent();
            String userName = intent.getStringExtra("userName");
            String userPwd = intent.getStringExtra("userPwd");
            et_username.setText(userName);
            et_userpass.setText(userPwd);
        }

    }
}
