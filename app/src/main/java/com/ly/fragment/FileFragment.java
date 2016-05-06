package com.ly.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.ly.entity.Book;
import com.ly.happyreader.R;
import com.ly.utils.SdcardUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ly on 2016/4/30.
 */
public class FileFragment extends Fragment {

    private TextView tv_title;
    private ListView listiew_file;
    private int currPosition = -1;

    // 记录当前的父文件夹
    File currentParent;

    // 记录当前路径下的所有文件夹的文件数组
    File[] currentFiles = null;
    List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    private SimpleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        // 定义一个SimpleAdapter
        adapter = new SimpleAdapter(
                getContext(), listItems, R.layout.layout_file_item,
                new String[]{"filename", "icon"}, new int[]{
                R.id.file_name, R.id.icon,});

        // 填充数据集
        listiew_file.setAdapter(adapter);
        initData();
        tv_title.setText("当前路径为:" + currentParent.getAbsolutePath());
        listiew_file.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                // 如果用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile()) {
                    // 也可自定义扩展打开这个文件等 TODO
                    if (currentFiles[position].getName().endsWith(".txt")) {
                        currPosition = position;
                    }
                    return;
                }
                // 获取用户点击的文件夹 下的所有文件
                File[] tem = currentFiles[position].listFiles();
                if (tem == null || tem.length == 0) {

                    Toast.makeText(getContext(),
                            "当前路径不可访问或者该路径下没有文件", Toast.LENGTH_LONG).show();
                } else {
                    currPosition = -1;
                    // 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position];
                    // 保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tem;
                    tv_title.setText("当前路径为:" + currentParent.getAbsolutePath());
                    // 再次更新ListView
                    inflateListView(currentFiles);
                }

            }
        });

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!currentParent.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                        // 获取上一级目录
                        currentParent = currentParent.getParentFile();
                        // 列出当前目录下的所有文件
                        currentFiles = currentParent.listFiles();

                        tv_title.setText("当前路径为:" + currentParent.getAbsolutePath());
                        // 再次更新ListView
                        inflateListView(currentFiles);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 保存书籍到数据库
     *
     */
    public void saveToDb() {
        if (currPosition != -1) {
            DbUtils dbUtils = DbUtils.create(getContext());
            Book book = new Book();
            book.setSpace(currentFiles[currPosition].getTotalSpace());
            book.setName(currentFiles[currPosition].getName());
            book.setPath(currentFiles[currPosition].getPath());
            try {
                dbUtils.saveBindingId(book);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载数据源
     */
    private void initData() {
        if (SdcardUtils.sdcardIsOk()) {
            File sdcard = Environment.getExternalStorageDirectory();
            currentParent = sdcard;
            currentFiles = sdcard.listFiles();
            // 使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(currentFiles);
        }
    }

    /**
     * 当前目录下的内容进行填充
     *
     * @param files
     */
    private void inflateListView(File[] files) {
        List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < files.length; i++) {

            Map<String, Object> listItem = new HashMap<String, Object>();

            if (files[i].isDirectory()) {
                // 如果是文件夹就显示的图片为文件夹的图片
                listItem.put("icon", R.drawable.ic_action_send);
            } else {
                if (files[i].getName().endsWith(".txt")) {
                    listItem.put("icon", R.drawable.ic_search);
                }
            }
            // 添加一个文件名称
            listItem.put("filename", files[i].getName());

            listItems1.add(listItem);
        }
        listItems.clear();
        listItems.addAll(listItems1);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化
     *
     * @param view
     */
    private void init(View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        listiew_file = (ListView) view.findViewById(R.id.listiew_file);
    }
}
