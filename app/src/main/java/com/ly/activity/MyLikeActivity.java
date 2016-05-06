package com.ly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.adapter.StoryAdapter;
import com.ly.entity.Story;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;
import com.ly.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyLikeActivity extends BaseActivity {
    private Toolbar toolbar;
    private ListView listView;
    private ImageView iv_back;
    private StoryAdapter adapter;
    private List<Story> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like);
        init();
    }
    private void init() {
        initView();
        setListen();
    }

    private void setListen() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        adapter = new StoryAdapter(this, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyLikeActivity.this, StoryDetailActivity.class);
                Log.e("info lie", position + "==========");
                Story story = dataList.get(position);
                intent.putExtra("story", story);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取控件
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.activity_my_like_toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.my_like_listView);
        iv_back = (ImageView) findViewById(R.id.iv_back);
    }

    /**
     * 加载数据
     */
    private void initData() {
        HttpUtils httpUtils = new HttpUtils(10 * 1000);
        RequestParams requestParams = new RequestParams();
        requestParams.addQueryStringParameter("uid", ((ReadApplication)(getApplication())).getUser().getId());
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.MYLIKE_PATH, requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.e("info", responseInfo.result);
                        dataList.addAll(doResult(responseInfo.result));
                        Log.e("info", dataList.toString());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
    }

    /**
     * 处理回传的数据
     *
     * @param s
     */
    private List<Story> doResult(String s) {
        List<Story> storyList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            String resultCode = jsonObject.getString("resultCode");
            String msg = jsonObject.getString("msg");
            JSONArray data = jsonObject.getJSONArray("data");
            Story story = null;
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                story = gson.fromJson(data.getJSONObject(i).toString(), Story.class);
                storyList.add(story);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return storyList;
    }
}