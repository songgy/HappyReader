package com.ly.happyreader;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.activity.BaseActivity;
import com.ly.activity.RingActivity;
import com.ly.activity.StoryCommitActivity;
import com.ly.adapter.LocAdapter;
import com.ly.entity.Book;
import com.ly.fragment.SlideFragment;
import com.ly.helper.LocalBook;
import com.ly.helper.MarkHelper;
import com.ly.utils.BitmapUtils;
import com.ly.utils.Constants;
import com.ly.utils.PinyinListComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private SlidingMenu slidingMenu;
    private SlideFragment slideFragment;
    private List<Book> dataList = new ArrayList<>();//数据源

    public static final int TAKE_PHOTO = 1;//拍照后的结果
    public static final int RESULT_PHOTO = 2;//裁剪后的结果
    public static final int GET_PHOTO = 3;//相册获取的结果
    private AlertDialog alertDialog;
    private String img_path;

    private static final String TAG = "MainActivity";
    private SharedPreferences.Editor editor;
    private boolean isInit = false;
    private Intent it;
    private ListView list;
    private ArrayList<HashMap<String, Object>> listItem = null;
    private SimpleAdapter listItemAdapter = null;
    private LocAdapter locAdapter = null;
    private LocalBook localbook;
    private HashMap<String, Object> map = null;
    private MarkHelper markhelper;
    private AdapterView.AdapterContextMenuInfo menuInfo;
    private ProgressDialog mpDialog;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        toolbar.setNavigationIcon(R.drawable.ic_slide);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.showMenu();
            }
        });
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        initFloatActionBtn();
        initSlidingMenu();
        initData();
        setListen();
        initBook();
    }

    /**
     * 加载书籍
     */
    private void initBook() {
        // 读取名为"mark"的sharedpreferences
        sp = getSharedPreferences("mark", MODE_PRIVATE);
        isInit = sp.getBoolean("isInit", false);
        localbook = new LocalBook(this, "localbook");
        // 添加长按点击
        list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE, 0, 0, "从阅读列表中删除");
            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    showProgressDialog("正在加载电子书...");
                    // 修改数据库中图书的最近阅读状态为1
                    String s = (String) listItem.get(arg2).get("path");
                    SQLiteDatabase db = localbook.getWritableDatabase();

                    File f = new File(s);
                    if (f.length() == 0) {
                        Toast.makeText(MainActivity.this, "该文件为空文件", Toast.LENGTH_SHORT).show();
                        if (mpDialog != null) {
                            mpDialog.dismiss();
                        }
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("now", 1);// key为字段名，value为值
                        db.update("localbook", values, "path=?", new String[] { s });// 修改状态为图书被已被导入
                        db.close();
                        String path = (String) listItem.get(arg2).get("path");
                        it = new Intent();
                        it.setClass(MainActivity.this, Read.class);
                        it.putExtra("aaa", path);
                        startActivity(it);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "list.setOnItemClickListener-> SQLException error", e);
                } catch (Exception e) {
                    Log.e(TAG, "list.setOnItemClickListener Exception", e);
                }
            }
        });
    }

    /**
     * 设置监听
     */
    private void setListen() {

    }

    /**
     * 加载数据源
     */
    private void initData() {
        if (((ReadApplication) (getApplication())).getBookList() != null) {
            dataList.addAll(((ReadApplication) (getApplication())).getBookList());
        }
    }

    /**
     * 查找控件
     */
    private void findView() {

        list = (ListView) findViewById(R.id.listView_main);
    }

    /**
     * 加载侧滑菜单
     */
    private void initSlidingMenu() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMenu(R.layout.layout_slide_menu);
        slideFragment = new SlideFragment();
        slideFragment.setFirstpageCallbackListener(new SlideFragment.FirstpageCallback() {
            @Override
            public void doBack() {
                slidingMenu.toggle();
            }

            @Override
            public void doEdit() {
                alertMyDialog();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, slideFragment).commit();
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindWidth((int) (300 * getResources().getDisplayMetrics().density));
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
    }

    /**
     * 加载悬浮按钮
     */
    private void initFloatActionBtn() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, StoryCommitActivity.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //添加书籍
        if (id == R.id.action_settings) {
            // 进入导入图书界面
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, InActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }
        //圈子
        if (id == R.id.action_ring) {
            startActivity(new Intent(MainActivity.this, RingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 自定义弹框
     */
    private void alertMyDialog() {
//        View view = getLayoutInflater().inflate(R.layout.activity_story_alertdialog, null);
//        TextView tv_galley = (TextView) view.findViewById(R.id.dialog_tv_gallley);
//        TextView tv_takephoto = (TextView) view.findViewById(R.id.dialog_tv_takephoto);
//        TextView tv_cancel = (TextView) view.findViewById(R.id.dialog_tv_cancel);
//        tv_galley.setOnClickListener(this);
//        tv_takephoto.setOnClickListener(this);
//        tv_cancel.setOnClickListener(this);

//        alertDialog = new AlertDialog.Builder(this)
//                .setView(view)
//                .create();
//        alertDialog.show();
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("照片选择")
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPhoto();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                })
                .setNeutralButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto();
                    }
                })
                .create();
        alertDialog.show();
    }

    /**
     * 获取从相册图片
     */
    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");//查询类型
        startActivityForResult(intent, GET_PHOTO);

    }

    /**
     * 拍照
     */
    private void takePhoto() {
        //打开相机的Action
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //该参数是拍照以后存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.CACHE_PATH, "tmp.jpg")));
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://拍照返回的回调函数
                File file = new File(Constants.CACHE_PATH, "tmp.jpg");
                startZoom(Uri.fromFile(file));
                break;
            case RESULT_PHOTO://裁剪过后的图片返回值
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = bundle.getParcelable("data"); //获取返回的图片
                try {
                    //保存到本地
                    FileOutputStream fos = new FileOutputStream(Constants.CACHE_PATH + "/tmp1.jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    img_path = Constants.CACHE_PATH + "/tmp1.jpg";
                    alertDialog.cancel();
                    // 此处可以把Bitmap保存到sd卡中
                    HttpUtils httpUtils = new HttpUtils();
                    RequestParams params = new RequestParams();
                    params.addQueryStringParameter("uid", ((ReadApplication) getApplication()).getUser().getId());
                    params.addBodyParameter("img", new File(img_path));
                    httpUtils.send(HttpRequest.HttpMethod.POST, Constants.SEVER_PATH + "/modifyPort.do", params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Log.i("--------------------", responseInfo.result);
                            // 把图片显示在ImageView控件上
                            try {
                                JSONObject obj = new JSONObject(responseInfo.result);
                                slideFragment.getIv_portrait().setImageBitmap(BitmapUtils.getCircleBimap(bitmap));
                                ((ReadApplication) getApplication()).getUser().setPortrait(obj.getString("data"));
                                //保存到本地
                                sp = getSharedPreferences("user", MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString("portrait", obj.getString("data"));
                                edit.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {

                        }
                    });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case GET_PHOTO://相册里面获取图片
                startZoom(data.getData());
                break;
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void startZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");//打开要裁剪的页面
        intent.setDataAndType(uri, "image/*");//裁剪的图片，和图片类型
        intent.putExtra("aspectX", 1);  //表示裁剪图片的宽高比例
        intent.putExtra("aspectY", 1);  //表示裁剪图片的宽高比例
        intent.putExtra("outputX", 300);//表示裁剪出来缩略图的宽高
        intent.putExtra("outputY", 300);//表示裁剪出来缩略图的宽高
        intent.putExtra("crop", true);//表示是否需要裁剪
        intent.putExtra("return-data", true);//表示返回裁剪后的图片
        startActivityForResult(intent, RESULT_PHOTO);
    }

    /**
     * 获取图片路径
     *
     * @param originalUri
     * @return
     */
    private String getPath(Uri originalUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        //android多媒体数据库的封装接口
        Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
        //按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        return path;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.dialog_tv_gallley:
//                getPhoto();
//                break;
//            case R.id.dialog_tv_takephoto:
//                takePhoto();
//                break;
//            case R.id.dialog_tv_cancel:
//                alertDialog.cancel();
//                break;
        }
    }

    /**
     * 加载listview 的adapter
     */
    private void adapter() {
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
                R.layout.item,// ListItem的XML实现
                // 动态数组与ImageItem对应的子项
                new String[] { "itemback", "ItemImage", "BookName", "ItemTitle", "ItemTitle1", "ItemTitle2", "ItemImage9", "LastImage" },
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] { R.id.itemback, R.id.ItemImage, R.id.bookName, R.id.ItemTitle, R.id.ItemTitle1, R.id.ItemTitle2, R.id.ItemImage9, R.id.last });
        // 添加并且显示
        list.setAdapter(listItemAdapter);
    }

    public void closeProgressDialog() {
        if (mpDialog != null) {
            mpDialog.dismiss();
        }
    }


    /**
     * 获取SD卡根目录
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 本地书库载入
     */
    public void local() {
        SQLiteDatabase db = localbook.getReadableDatabase();
        String col[] = { "path" };
        Cursor cur = db.query("localbook", col, "type=1", null, null, null, null);
        Cursor cur1 = db.query("localbook", col, "type=2", null, null, null, null);
        Integer num = cur.getCount();
        Integer num1 = cur1.getCount();
        ArrayList<String> arraylist = new ArrayList<String>();
        while (cur1.moveToNext()) {
            String s = cur1.getString(cur1.getColumnIndex("path"));
            arraylist.add(s);
        }
        while (cur.moveToNext()) {
            String s = cur.getString(cur.getColumnIndex("path"));
            arraylist.add(s);
        }
        db.close();
        cur.close();
        cur1.close();
        if (listItem == null)
            listItem = new ArrayList<HashMap<String, Object>>();
        listItem.clear();
        String[] bookids = getResources().getStringArray(R.array.bookid);
        String[] booknames = getResources().getStringArray(R.array.bookname);
        String[] bookauthors = getResources().getStringArray(R.array.bookauthor);
        Map<String, String[]> maps = new HashMap<String, String[]>();
        for (int i = 0; i < bookids.length; i++) {
            String[] value = new String[2];
            value[0] = booknames[i];
            value[1] = bookauthors[i];
            maps.put(bookids[i], value);
        }
        for (int i = 0; i < num + num1; i++) {
            if (i < num1) {
                File file1 = new File(arraylist.get(i));
                String m = file1.getName().substring(0, file1.getName().length() - 4);
                if (m.length() > 8) {
                    m = m.substring(0, 8) + "...";
                }
                String id = arraylist.get(i).substring(arraylist.get(i).lastIndexOf("/") + 1);
                String[] array = maps.get(id);
                String auther = array != null && array[1] == null ? "未知" : array[1];
                String name = array[0] == null ? m : array[0];
                map = new HashMap<String, Object>();

                if (i == 0) {
                    map.put("itemback", R.drawable.itemback);
                } else if ((i % 2) == 0) {
                    map.put("itemback", R.drawable.itemback);
                }
                map.put("ItemImage", R.drawable.cover);
                map.put("BookName", "");
                map.put("ItemTitle", name == null ? m : name);
                map.put("ItemTitle1", "作者：" + auther);
                map.put("LastImage", "推荐书目");
                map.put("path", file1.getPath());
                map.put("com", 0 + file1.getName());// 单独用于排序
                listItem.add(map);
            } else {
                map = new HashMap<String, Object>();

                File file1 = new File(arraylist.get(i));
                String m = file1.getName().substring(0, file1.getName().length() - 4);
                if (m.length() > 8) {
                    m = m.substring(0, 8) + "...";
                }
                if (i == 0) {
                    map.put("itemback", R.drawable.itemback);
                } else if ((i % 2) == 0) {
                    map.put("itemback", R.drawable.itemback);
                }
                map.put("ItemImage", R.drawable.cover);
                map.put("BookName", m);
                map.put("ItemTitle", m);
                map.put("ItemTitle1", "作者：未知");
                map.put("LastImage", "本地导入");
                map.put("path", file1.getPath());
                map.put("com", "1");
                listItem.add(map);
            }
        }
        Collections.sort(listItem, new PinyinListComparator());
        if (locAdapter == null) {
            locAdapter = new LocAdapter(this, listItem, num1);
            list.setAdapter(locAdapter);
        }
        locAdapter.notifyDataSetChanged();
    }

    /**
     * 长按菜单响应函数
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:// 删除文件
                // 获取点击的是哪一个文件
                menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                HashMap<String, Object> imap = listItem.get(menuInfo.position);
                String path0 = (String) imap.get("path");
                SQLiteDatabase db = localbook.getWritableDatabase();
                try {
                    ContentValues values = new ContentValues();
                    values.put("now", 0);// key为字段名，value为值
                    values.put("type", 0);
                    values.putNull("ready");
                    // db.update("localbook", values, "path=?", new String[] { s
                    // });// 修改状态为图书被已被导入
                    db.update("localbook", values, "path=? and type=1", new String[] { path0 });// 修改状态为图书被已被导入
                    // 清空对本书的记录
                    editor = sp.edit();
                    editor.remove(path0 + "jumpPage");
                    editor.remove(path0 + "count");
                    editor.remove(path0 + "begin");
                    editor.commit();
                    markhelper = new MarkHelper(this);
                    // 删除数据库书签记录
                    SQLiteDatabase db2 = markhelper.getWritableDatabase();
                    db2.delete("markhelper", "path='" + path0 + "'", null);
                    db2.close();
                } catch (SQLException e) {
                    Log.e(TAG, "onContextItemSelected-> SQLException error", e);
                } catch (Exception e) {
                    Log.e(TAG, "onContextItemSelected-> Exception error", e);
                }
                db.close();
                // 重新载入页面
                local();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        closeProgressDialog();
        super.onDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent itt = getIntent();
        String ss = itt.getStringExtra("nol");
        Boolean bad = itt.getBooleanExtra("bad", false);
        if (bad) {
            Toast.makeText(MainActivity.this, "打开失败", Toast.LENGTH_SHORT).show();
        }
        // 判断是从哪里回退进主界面的 并重新载入 以此实现同步数据
        if (ss != null) {
            if (ss.equals("n")) {
                local();
            } else {
                local();
            }
        } else {
            local();
        }
    }


    public void showProgressDialog(String msg) {
        mpDialog = new ProgressDialog(MainActivity.this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
        mpDialog.setMessage(msg);
        mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
        mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        mpDialog.show();
    }

}
