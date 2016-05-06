package com.ly.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.adapter.StoryAdapter;
import com.ly.entity.Story;
import com.ly.happyreader.R;
import com.ly.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RingActivity extends BaseActivity {
    private PullToRefreshListView listView;
    private StoryAdapter adapter;
    private List<Story> dataList = new ArrayList<>();
    private int pageCount = 0;
    private boolean isRefresh = false;//正在刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("圈子");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        listView = (PullToRefreshListView) findViewById(R.id.fragment_story_listView);
        initData();
        adapter = new StoryAdapter(this, dataList);
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!isRefresh) {
                    isRefresh = true;
                    initData();
                } else {
                    listView.onRefreshComplete();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RingActivity.this, StoryDetailActivity.class);
                Log.e("info lie", position + "==========");
                Story story = dataList.get(position - 1);
                intent.putExtra("story", story);
                startActivity(intent);
            }
        });
    }

    /**
     * 加载数据
     */
    private void initData() {
        HttpUtils httpUtils = new HttpUtils(10 * 1000);
        RequestParams requestParams = new RequestParams();
        requestParams.addQueryStringParameter("pageCount", pageCount++ + "");
        httpUtils.send(HttpRequest.HttpMethod.GET, Constants.SEVER_PATH + "/story.do", requestParams,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        listView.onRefreshComplete();
                        isRefresh = false;
                        Log.e("info", responseInfo.result);
                        dataList.addAll(doResult(responseInfo.result));
                        Log.e("aaainfo----", dataList.toString());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ring, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //添加书籍
        if (id == R.id.action_ring) {
            startActivity(new Intent(RingActivity.this, StoryCommitActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
