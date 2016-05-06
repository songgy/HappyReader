package com.ly.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ly.activity.LoginActivity;
import com.ly.activity.MyLikeActivity;
import com.ly.activity.MySendActivity;
import com.ly.activity.RingActivity;
import com.ly.entity.User;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;
import com.ly.utils.ActivityController;
import com.ly.utils.Constants;
import com.ly.utils.NeedLoginUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by ly on 2016/4/29.
 */
public class SlideFragment extends Fragment implements View.OnClickListener {

    private LinearLayout ll_firstpage;
    private LinearLayout ll_user;
    private LinearLayout ll_comment;
    private LinearLayout ll_collection;
    private TextView tv_nickName;
    private ImageView iv_portrait;
    private TextView tv_setting;
    private FirstpageCallback firstpageCallback;
    private User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    /**
     * 初始化
     *
     * @param view
     */
    private void init(View view) {
        findView(view);
        setListen();
        user = ((ReadApplication) getActivity().getApplication()).getUser();
        if (user != null) {
            tv_nickName.setText(user.getNickName());
            if (!TextUtils.isEmpty(user.getPortrait())) {
                Picasso.with(getContext()).load(Constants.PORTRAIT_PATH + "/" + user.getPortrait()).into(iv_portrait);
            }
        }
    }

    /**
     * 设置监听
     */
    private void setListen() {
        ll_firstpage.setOnClickListener(this);
        ll_user.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_collection.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        iv_portrait.setOnClickListener(this);
        tv_nickName.setOnClickListener(this);
    }

    /**
     * 加载布局
     *
     * @param view
     */
    private void findView(View view) {
        ll_firstpage = (LinearLayout) view.findViewById(R.id.ll_firstpage);
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        ll_comment = (LinearLayout) view.findViewById(R.id.ll_comment);
        ll_collection = (LinearLayout) view.findViewById(R.id.ll_collection);
        tv_nickName = (TextView) view.findViewById(R.id.tv_nickName);
        tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        iv_portrait = (ImageView) view.findViewById(R.id.iv_portrait);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_firstpage:
                firstpageCallback.doBack();
                break;
            case R.id.ll_user:
                if (user != null) {
                    firstpageCallback.doEdit();
                } else {
                    startActivity(new Intent(getContext(), RingActivity.class));
                }
                break;
            case R.id.ll_comment:
                if (NeedLoginUtils.needLoginOrNot(getActivity().getApplication())) {
                    NeedLoginUtils.popDialog(getContext());
                } else {
                    startActivity(new Intent(getContext(), MySendActivity.class));
                }
                break;
            case R.id.ll_collection:
                if (NeedLoginUtils.needLoginOrNot(getActivity().getApplication())) {
                    NeedLoginUtils.popDialog(getContext());
                } else {
                    startActivity(new Intent(getContext(), MyLikeActivity.class));
                }
                break;
            case R.id.tv_setting:
                ActivityController.finishAll();
                break;
            case R.id.tv_nickName:
                if (NeedLoginUtils.needLoginOrNot(getActivity().getApplication())) {
                    NeedLoginUtils.popDialog(getContext());
                } else {
                    ExitApp();
                }
                break;
            case R.id.iv_portrait:
                if (NeedLoginUtils.needLoginOrNot(getActivity().getApplication())) {
                    NeedLoginUtils.popDialog(getContext());
                } else {
                    firstpageCallback.doEdit();
                }
                break;
        }
    }

    /**
     * 退出应用
     */
    private void ExitApp() {
        NeedLoginUtils.popExitDialog(getContext(), getActivity().getApplication());
    }

    /**
     * 登录弹框
     */
    private void login() {
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     * 回掉接口
     */
    public interface FirstpageCallback {
        void doBack();//设置关闭事件

        void doEdit();//头像修改
    }

    public void setFirstpageCallbackListener(FirstpageCallback firstpageCallback) {
        this.firstpageCallback = firstpageCallback;
    }

    public ImageView getIv_portrait() {
        return iv_portrait;
    }
}
