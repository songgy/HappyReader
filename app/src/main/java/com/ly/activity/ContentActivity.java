package com.ly.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.ly.entity.Book;
import com.ly.fragment.ListFragment;
import com.ly.fragment.SlideFragment;
import com.ly.happyreader.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back;//上部返回栏
    private ImageView iv_back;//上部返回键
    private FrameLayout frameLayout;//内容布局
    private LinearLayout ll_menu;//底部菜单
    private TextView tv_last;//上一章
    private TextView tv_next;//下一章
    private SeekBar seekBar;//进度调节器
    private ImageView iv_mulu;//文章目录
    private SlidingMenu slidingMenu;
    private ListFragment listFragment;
    private Book book;
    private long currPosition = 0;//当前书本的位置
    private int size = 1500;//当前页的大小
    private DbUtils dbUtils;//数据库操作对象
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 100://隐藏菜单
                    ll_back.setVisibility(View.GONE);
                    ll_menu.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    });

    /**
     *
     */
    private void init() {
        dbUtils = DbUtils.create(this);
        findView();
        initBook();
        setListen();
        initSlidingMenu();
//        initList();
    }

    /**
     * 加载目录
     */
    private void initList() {

    }

    //加载书籍
    private void initBook() {
        seekBar.setMax(100);
        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");
        if (book.getSeek() != 0) {
            currPosition = book.getSeek();
        }
        //加载当前位置
        yuedu(currPosition);
    }

    /**
     * 阅读书籍下一页
     *
     * @throws IOException
     * @throws DbException
     */

    private void yuedu(long postion) {
        RandomAccessFile bookFile = null;
        try {
            byte[] buf = new byte[size];//当前页
            bookFile = new RandomAccessFile(book.getPath(), "r");
            bookFile.seek(postion);
            bookFile.read(buf);
            bookFile.close();
            Log.e("info", "当前的" + postion);
            Log.e("progress", new String(buf) + "======");
            tv_content.setText(new String(buf));
            currPosition += size;
            book.setSeek(postion);

            //更新进度
            dbUtils.update(book, "seek");

            seekBar.setProgress((int) (book.getSeek() * 100.0 / book.getSpace()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 阅读书籍上一页
     *
     * @throws IOException
     * @throws DbException
     */
    private void yueduReduce() {
        RandomAccessFile bookFile = null;
        try {
            byte[] buf = new byte[size];//当前页
            bookFile = new RandomAccessFile(book.getPath(), "r");
            bookFile.seek(currPosition - size);
            bookFile.read(buf);
            bookFile.close();

            tv_content.setText(new String(buf));
            currPosition -= size;
            book.setSeek(currPosition);
            //更新进度
            dbUtils.update(book, "seek");

            seekBar.setProgress((int) (book.getSeek() * 100.0 / book.getSpace()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载侧滑菜单
     */
    private void initSlidingMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMenu(R.layout.layout_slide_menu);
        listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, listFragment).commit();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindWidth((int) (300 * getResources().getDisplayMetrics().density));
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
    }


    /**
     * 设置监听
     */
    private void setListen() {
        iv_back.setOnClickListener(this);
        tv_last.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        iv_mulu.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currPosition = (long) (book.getSpace() * progress / 100.0);

                    seekBar.setProgress(progress);

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
                        int height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
                        Log.e("info  xy", x + "    " + y);
                        Log.e("info  wh", width + "    " + height);
                        //出现菜单
                        if (y > height / 3 && y < height * 2 / 3) {
                            ll_back.setVisibility(View.VISIBLE);
                            ll_menu.setVisibility(View.VISIBLE);
                            handler.sendEmptyMessageDelayed(100, 3000);
                        } else {
                            //上一页
                            if (x < width / 2) {

                                yueduReduce();
                            } else {//下一页
                                yuedu(currPosition);
                            }
                        }

                        break;
                }
                return false;
            }
        });
    }

    /**
     * 查找控件
     */
    private void findView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
        tv_last = (TextView) findViewById(R.id.tv_last);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_content = (TextView) findViewById(R.id.tv_content);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        iv_mulu = (ImageView) findViewById(R.id.iv_mulu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_last:
                break;
            case R.id.tv_next:
                break;
            case R.id.iv_mulu:
                slidingMenu.showMenu();
                break;
        }
    }
}