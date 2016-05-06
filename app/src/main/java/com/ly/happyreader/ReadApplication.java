package com.ly.happyreader;

import android.app.Application;
import android.os.Environment;

import com.ly.entity.Book;
import com.ly.entity.User;
import com.ly.utils.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by ly on 2016/4/26.
 */
public class ReadApplication extends Application {

    private User user;

    private List<Book> bookList;//书架

    @Override
    public void onCreate() {
        super.onCreate();
        initUIL();
        initCacheDir();
    }

    /**
     * 加载图片路径
     */
    private void initCacheDir() {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File dir = new File(Constants.CACHE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * 初始化UIL
     */
    private void initUIL() {

        File directory = StorageUtils.getCacheDirectory(this);

        ImageLoaderConfiguration conn = new ImageLoaderConfiguration.Builder(this)
                .diskCache(new LimitedAgeDiskCache(directory, 50 * 1024 * 1024))
                .memoryCache(new WeakMemoryCache())
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .threadPoolSize(3)
                .threadPriority(3)
                .build();

        ImageLoader.getInstance().init(conn);

    }

    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}
