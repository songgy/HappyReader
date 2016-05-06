package com.ly.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.ly.happyreader.R;
import com.ly.happyreader.ReadApplication;
import com.ly.utils.Constants;
import com.ly.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class StoryCommitActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView tv_cancel;
    private TextView tv_sure;
    private EditText ed_title;
    private EditText ed_content;
    private ImageView iv_add;
    private AlertDialog alertDialog;
    private String img_path;

    public static final int TAKE_PHOTO = 1;//拍照后的结果
    public static final int RESULT_PHOTO = 2;//裁剪后的结果
    public static final int GET_PHOTO = 3;//相册获取的结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_commit);
        toolbar = (Toolbar) findViewById(R.id.activity_story_toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        setListen();
    }

    /**
     * 设置监听
     */
    private void setListen() {
        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        iv_add.setOnClickListener(this);
    }

    /**
     * 查找控件
     */
    private void findView() {
        tv_cancel = (TextView) findViewById(R.id.activity_story_tv_cancel);
        tv_sure = (TextView) findViewById(R.id.activity_story_tv_sure);
        ed_title = (EditText) findViewById(R.id.activity_story_et_title);
        ed_content = (EditText) findViewById(R.id.activity_story_et_content);
        iv_add = (ImageView) findViewById(R.id.activity_story_iv_add);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_story_tv_cancel://关闭activity
                finish();
                break;
            case R.id.activity_story_tv_sure:
                addStory();
                break;
            case R.id.activity_story_iv_add:
                alertMyDialog();
                break;

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
     * 提交story
     */
    private void addStory() {
        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(StoryCommitActivity.this, "请输入标题和内容", Toast.LENGTH_SHORT).show();
        } else {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams params = new RequestParams("utf-8");
            params.addQueryStringParameter("title", title);
            params.addQueryStringParameter("content", content);

            params.addBodyParameter("uid", ((ReadApplication) (getApplication())).getUser().getId());
            params.addQueryStringParameter("story_time", DateUtil.miilToString(System.currentTimeMillis()));
            if (TextUtils.isEmpty(img_path)) {
                params.addQueryStringParameter("fileExist", 0 + "");
            } else {
                params.addQueryStringParameter("fileExist", 1 + "");
                params.addBodyParameter("story_img", new File(img_path));
            }
            Log.e("indo", img_path);
            httpUtils.send(HttpRequest.HttpMethod.POST, Constants.SEVER_PATH + "/storyadd.do", params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseInfo.result);
                        if ("1".endsWith(jsonObject.getString("resultCode"))) {
                            setResult(RESULT_OK);
                            Log.e("info", jsonObject.getString("msg"));
                            finish();
                        } else {
                            Toast.makeText(StoryCommitActivity.this, "发表失败，请重试！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(StoryCommitActivity.this, "发表失败，请重试！" + s, Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    /*    alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();*/
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
                Bitmap bitmap = bundle.getParcelable("data"); //获取返回的图片
                try {
                    //保存到本地
                    FileOutputStream fos = new FileOutputStream(Constants.CACHE_PATH + "/tmp1.jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    img_path = Constants.CACHE_PATH + "/tmp1.jpg";
                    alertDialog.cancel();
                    iv_add.setImageBitmap(bitmap);

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
}
