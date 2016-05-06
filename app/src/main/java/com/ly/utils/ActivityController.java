package com.ly.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by ly on 2016/4/8.
 */
public class ActivityController {
    private static ArrayList<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishAll() {
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    public static void finishSome(int numebr) {
        for (int i = 0; i < numebr; i++) {
            Activity activity = activityList.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
