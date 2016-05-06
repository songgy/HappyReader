package com.kaixinbook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.kaixinbook.adapter.LocAdapter;
import com.kaixinbook.helper.LocalBook;
import com.kaixinbook.helper.MarkHelper;
import com.kaixinbook.mydialog.AboutDialog;
import com.kaixinbook.util.PinyinListComparator;
import com.ly.myapplication.R;

/**
 * 程序界面
 *
 * @author
 *
 */

public class BookListActivity extends Activity {
	private static final String TAG = "MainActivity";
	// private Boolean a = true, b = false, c = false;
	private SharedPreferences.Editor editor;
	private boolean isInit = false;
	private Intent it;
	private ListView list;
	private ArrayList<HashMap<String, Object>> listItem = null;
	private SimpleAdapter listItemAdapter = null;
	ListView listView;
	private LocAdapter locAdapter = null;
	private LocalBook localbook;
	private HashMap<String, Object> map = null;
	private Map<String, Integer[]> map2;// 存放本地推荐目录的小封面图片引用
	private MarkHelper markhelper;
	protected ProgressDialog mDialog = null;
	private int[] menu_toolbar_image_array = { R.drawable.reading, R.drawable.local, R.drawable.favourite };
	private String[] menu_toolbar_name_array = { "最近阅读", "本地书库", "我的收藏" };
	private AdapterView.AdapterContextMenuInfo menuInfo;
	View menuView;
	private ProgressDialog mpDialog;

	private SharedPreferences sp;

	private TextView titletext, titletext1;

	private GridView toolbarGrid;

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
	 * 工具栏adapter的获取
	 *
	 * @param menuNameArray
	 * @param imageResourceArray
	 * @return simperAdapter
	 */
	private SimpleAdapter getMenuAdapter(String[] menuNameArray, int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data, R.layout.item_menu, new String[] { "itemImage", "itemText" }, new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}
	/**
	 * 获取SD卡根目录
	 *
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
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
				map.put("ItemImage", map2 != null ? map2.get(file1.getName())[0] : R.drawable.cover);
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		map2 = new HashMap<String, Integer[]>();
		String[] bookids = getResources().getStringArray(R.array.bookid);
		for (int i = 0; i < bookids.length; i++) {
			map2.put(bookids[i], new Integer[] { R.drawable.book0 + i });
		}

		// 载入工具条 创建底部菜单 Toolbar
		toolbarGrid = (GridView) findViewById(R.id.GridView_toolbar);
		// 设置背景
		toolbarGrid.setBackgroundResource(R.drawable.channelgallery1);
		// 设置每行列数
		toolbarGrid.setNumColumns(3);
		// 位置居中
		toolbarGrid.setGravity(Gravity.CENTER);
		// 垂直间隔
		toolbarGrid.setVerticalSpacing(10);
		// 水平间隔
		toolbarGrid.setHorizontalSpacing(10);
		// 设置菜单Adapter
		toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array, menu_toolbar_image_array));
		// 读取名为"mark"的sharedpreferences
		sp = getSharedPreferences("mark", MODE_PRIVATE);
		isInit = sp.getBoolean("isInit", false);
		list = (ListView) findViewById(R.id.ListView01);
		titletext = (TextView) findViewById(R.id.titletext);
		titletext1 = (TextView) findViewById(R.id.titletext1);
		localbook = new LocalBook(this, "localbook");
		// 添加长按点击
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.add(Menu.NONE, 0, 0, "从阅读列表中删除");
			}

		});

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				try {
					showProgressDialog("正在加载电子书...");
					// 修改数据库中图书的最近阅读状态为1
					String s = (String) listItem.get(arg2).get("path");
					SQLiteDatabase db = localbook.getWritableDatabase();

					File f = new File(s);
					if (f.length() == 0) {
						Toast.makeText(BookListActivity.this, "该文件为空文件", Toast.LENGTH_SHORT).show();
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
						it.setClass(BookListActivity.this, Read.class);
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
		// 工具栏的点击事件处理
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (arg2) {
					case 1:

						toolbarGrid.setBackgroundResource(R.drawable.channelgallery2);
						local();
						break;
					case 2:
						titletext.setText("我的收藏");
						toolbarGrid.setBackgroundResource(R.drawable.channelgallery3);
						listItem = new ArrayList<HashMap<String, Object>>();
						adapter();
						titletext1.setText("(0)");
						break;
				}
			}
		});
	}

	/**
	 * 调用系统menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		closeProgressDialog();
		super.onDestroy();
		finish();
	}

	/**
	 * 处理menu弹出后点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int it = item.getItemId();
		switch (it) {
			case R.id.it1:
				// 进入导入图书界面
				Intent intent = new Intent();
				intent.setClass(BookListActivity.this, InActivity.class);
				startActivity(intent);
				this.finish();
				break;
			case R.id.it2:
				// 进入反馈界面
				break;
			case R.id.it3:
				// 进入关于界面
				AboutDialog about;
				about = new AboutDialog(BookListActivity.this, R.style.FullHeightDialog);
				about.show();
				about.setMessage1(getString(R.string.about_book));
				about.setMessage2(getString(R.string.app_name));
				about.setMessage3(getString(R.string.aboue_text1));
				break;
		}
		return super.onOptionsItemSelected(item);
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
		String bb = itt.getStringExtra("bbb");
		Boolean bad = itt.getBooleanExtra("bad", false);
		if (bad) {
			Toast.makeText(BookListActivity.this, "打开失败", Toast.LENGTH_SHORT).show();
		}
		// 判断是否由快捷方式进入的程序
		if (bb != null) {

			it = new Intent();
			it.setClass(BookListActivity.this, Read.class);
			it.putExtra("aaa", bb);
			it.putExtra("ccc", "ccc");
			startActivity(it);
			this.finish();
		}
		// 判断是从哪里回退进主界面的 并重新载入 以此实现同步数据
		if (ss != null) {
			if (ss.equals("n")) {
				local();
			} else {
				toolbarGrid.setBackgroundResource(R.drawable.channelgallery2);
				local();
			}
		} else {
			local();
		}
	}


	public void showProgressDialog(String msg) {
		mpDialog = new ProgressDialog(BookListActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		mpDialog.setMessage(msg);
		mpDialog.setIndeterminate(false);// 设置进度条是否为不明确
		mpDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
		mpDialog.show();
	}
}