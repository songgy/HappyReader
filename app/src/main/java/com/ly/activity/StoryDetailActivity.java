package com.ly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.adapter.CommentAdapter;
import com.ly.entity.Comment;
import com.ly.entity.Story;
import com.ly.entity.User;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;
import com.ly.utils.Constants;
import com.ly.utils.DateUtil;
import com.ly.utils.NeedLoginUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoryDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private ImageView iv_share;
    private ImageView iv_portrait;
    private ImageView iv_sex;
    private TextView tv_title;
    private TextView tv_comment_count;
    private TextView tv_like_count;
    private TextView tv_content;
    private TextView tv_nickName;
    private TextView tv_story_time;
    private ImageView iv_comment;
    private RadioButton rb_like;
    private ListView listview;
    private List<Comment> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private ImageView iv_story_img;
    private Story story;
    private boolean isLiked = false;
    private EditText ed_comment_content;
    private Button btn_ok;
    private LinearLayout ll_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        setListen();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        story = (Story) intent.getSerializableExtra("story");
        User user = story.getUser();
        Log.e("-----", user + "");
        tv_title.setText(story.getTitle());
        tv_nickName.setText(user.getNickName());
        if (!TextUtils.isEmpty(user.getPortrait())) {

            Picasso.with(this).load(Constants.PORTRAIT_PATH + "/" + user.getPortrait()).into(iv_portrait);
        }
        if (!TextUtils.isEmpty(story.getStory_img())) {
            Picasso.with(this).load(Constants.IMG_PATH + "/" + story.getStory_img()).into(iv_story_img);
        }
        if (user.getSex().endsWith("0")) {
            iv_sex.setImageResource(R.drawable.registermale2x);
        } else {
            iv_sex.setImageResource(R.drawable.registerfemale2x);
        }
        tv_story_time.setText(story.getStory_time());
        tv_content.setText(story.getContent());
        tv_like_count.setText(story.getLike_count());
        tv_comment_count.setText(story.getComment_count());
        initComment(story.getId());
        adapter = new CommentAdapter(this, commentList);
        listview.setAdapter(adapter);

    }

    /**
     * 加载评论
     *
     * @param id
     */
    private void initComment(String id) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams("utf-8");
        params.addQueryStringParameter("story_id", id);
        httpUtils.send(HttpRequest.HttpMethod.POST, Constants.SEVER_PATH + "/comments.do", params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                commentList.addAll(doResult(responseInfo.result));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    /**
     * 处理返回结果
     *
     * @param result
     */
    private List<Comment> doResult(String result) {
        List<Comment> commentList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.optString("resultCode").equals("1")) {
                JSONArray array = obj.optJSONArray("data");
                Gson gson = new Gson();
                Comment comment;
                for (int i = 0; i < array.length(); i++) {
                    comment = gson.fromJson(array.optJSONObject(i).toString(), Comment.class);
                    commentList.add(comment);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    /**
     *
     */
    private void setListen() {
        iv_back.setOnClickListener(this);
        rb_like.setOnClickListener(this);
        iv_comment.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
    }

    /**
     * 查找控件
     */
    private void findView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        rb_like = (RadioButton) findViewById(R.id.rb_like);
        iv_story_img = (ImageView) findViewById(R.id.iv_story_img);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);

        iv_portrait = (ImageView) findViewById(R.id.iv_portrait);
        iv_sex = (ImageView) findViewById(R.id.iv_sex);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
        tv_like_count = (TextView) findViewById(R.id.tv_like_count);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_story_time = (TextView) findViewById(R.id.tv_story_time);
        listview = (ListView) findViewById(R.id.listview);
        ed_comment_content = (EditText) findViewById(R.id.ed_comment_content);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        btn_ok = (Button) findViewById(R.id.btn_ok);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                if (NeedLoginUtils.needLoginOrNot(getApplication())) {
                    NeedLoginUtils.popDialog(StoryDetailActivity.this);
                } else {
                    //TODO 分享
                }
                break;
            case R.id.rb_like:
                if (NeedLoginUtils.needLoginOrNot(getApplication())) {
                    NeedLoginUtils.popDialog(StoryDetailActivity.this);
                    rb_like.setChecked(false);
                } else {
                    if (isLiked) {
                        rb_like.setChecked(false);
                    }
                    doLike();
                }
                break;
            case R.id.iv_comment:
                if (NeedLoginUtils.needLoginOrNot(getApplication())) {
                    NeedLoginUtils.popDialog(StoryDetailActivity.this);
                } else {
                    doComment();
                }
                break;
            case R.id.btn_ok:
                sendComment();
                break;
        }
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        String content = ed_comment_content.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("uid", ((ReadApplication) (getApplication())).getUser().getId());
//            params.addQueryStringParameter("uid", 111 + "");
            params.addQueryStringParameter("story_id", story.getId());
            params.addQueryStringParameter("content", content);
            params.addHeader("Content-Type", "text/html");    //这行很重要
            params.addHeader("charset", "utf-8");
            params.addQueryStringParameter("comment_time", DateUtil.miilToStringSS(System.currentTimeMillis()));
            httpUtils.send(HttpRequest.HttpMethod.POST, Constants.SEVER_PATH + "/commentadd.do", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    try {
                        JSONObject object = new JSONObject(responseInfo.result);
                        String data = object.optString("data");
                        if ("1".equals(data)) {
                            tv_comment_count.setText(Integer.parseInt(tv_comment_count.getText().toString()) + 1 + "");
                            ll_comment.setVisibility(View.GONE);
                            initComment(story.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {

                }
            });
        } else {
            Toast.makeText(StoryDetailActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进行评论
     */
    private void doComment() {

        ll_comment.setVisibility(View.VISIBLE);
    }

    /**
     * 喜欢事件
     */
    private void doLike() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("uid", ((ReadApplication) (getApplication())).getUser().getId());
        params.addQueryStringParameter("story_id", story.getId());
        if (rb_like.isChecked()) {
            isLiked = true;
            params.addQueryStringParameter("liked", 1 + "");
        } else {
            isLiked = false;
            params.addQueryStringParameter("liked", 0 + "");
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, Constants.SEVER_PATH + "/like.do", params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {

                    JSONObject obj = new JSONObject(responseInfo.result);
                    String data = obj.optString("data");
                    if ("1".equals(data)) {
                        tv_like_count.setText(Integer.parseInt(tv_like_count.getText().toString()) + 1 + "");
                    } else {
                        tv_like_count.setText(Integer.parseInt(tv_like_count.getText().toString()) - 1 + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}
