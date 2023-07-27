package com.benyq.tikbili.utils;

import android.util.Log;

/**
 * @author benyq
 * @date 7/26/2023
 * 统计冷启动各个时间点的耗时
 */

public class StartLogHelper {

    private static final String TAG = "StartLogHelper";
    private static long startTime;

    /**
     * 设置开始时间，暂时放到启动的时间,放到Application的attachBaseContext方法中
     *
     * @param start
     */
    public static void setStart(long start) {
        startTime = start;
    }

    /**
     * application初始化的时间，放到Application的onCreate方法最后
     */
    public static void getApplicationTime() {
        long end = System.currentTimeMillis();
        Log.e(TAG, "==================getApplicationTime==" + (end - startTime));

    }

    /**
     * 欢迎页初始化的时间
     */
    public static void getWelcomeTime() {
        long end = System.currentTimeMillis();
        Log.e(TAG, "==================getWelcomeTime==" + (end - startTime));
    }

    /**
     * Main页面初始化的时间
     */

    public static void getMainTime() {
        long end = System.currentTimeMillis();
        Log.e(TAG, "==================getMainTime==" + (end - startTime));
    }
}