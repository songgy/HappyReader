package com.ly.utils;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ly.activity.LoginActivity;
import com.ly.happyreader.MainActivity;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;


/**
 * 检测需要登录工具类
 */
public class NeedLoginUtils {

    private static AlertDialog dialog;

    /**
     * 判断当前是否有用户登录
     *
     * @param application
     * @return true 需要登录   false 不需要登录
     */
    public static boolean needLoginOrNot(Application application) {
        if (((ReadApplication) application).getUser() != null) {
            String userName = ((ReadApplication) application).getUser().getUserName();
            if (TextUtils.isEmpty(userName)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * 弹出登录提示对话框
     *
     * @param context
     */
    public static void popDialog(final Context context) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_need_login_dialog, null);
//        init(view, context);
//        dialog = new AlertDialog.Builder(context)
//                .setView(view)
//                .create();
//        dialog.show();
        dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("请登录")
                .setNegativeButton("取消", null)
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 弹出登录提示对话框
     *
     * @param context
     */
    public static void popExitDialog(final Context context, final Application application) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_exit_dialog, null);
//        initExit(view, context, application);
//        dialog = new AlertDialog.Builder(context)
//                .setView(view)
//                .create();
//        dialog.show();
        dialog = new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("正在退出")
                .setNegativeButton("取消", null)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ReadApplication) application).setUser(null);

                        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("id", null);
                        edit.putString("birthday", null);
                        edit.putString("nickName", null);
                        edit.putString("portrait", null);
                        edit.putString("sex", null);
                        edit.putString("sign", null);
                        edit.putString("userName", null);
                        edit.putString("userPwd", null);
                        edit.commit();
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * 加载布局
     *
     * @param view
     * @param context
     */
    private static void init(View view, final Context context) {
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });
    }

    /**
     * 加载布局
     *
     * @param view
     * @param context
     */
    private static void initExit(View view, final Context context, final Application application) {
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReadApplication) application).setUser(null);

                SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("id", null);
                edit.putString("birthday", null);
                edit.putString("nickName", null);
                edit.putString("portrait", null);
                edit.putString("sex", null);
                edit.putString("sign", null);
                edit.putString("userName", null);
                edit.putString("userPwd", null);
                edit.commit();
                context.startActivity(new Intent(context, MainActivity.class));

            }
        });
    }

}
