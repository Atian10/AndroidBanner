package com.atian.banner.util;

import android.util.Log;

/**
 * 统一日志工具类，封装 android.util.Log
 * <p>所有日志输出统一通过本类调用，便于后续扩展日志级别控制与崩溃上报</p>
 */
public final class LogUtils {

    private LogUtils() {
        // 工具类禁止实例化
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}
