package com.ly.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ly on 2016/4/30.
 */
public class SdcardUtils {


    /**
     * 判断sd卡状态
     *
     * @return
     */
    public static boolean sdcardIsOk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


}
