package com.td.tddrivingbehavior;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MyApplication extends Application {

    protected static String TAG = "MyApplication";

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        //在Android 9 小米8SE启动app消除弹窗
        closeAndroidPDialog();

        //初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());

        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     */
    private void closeAndroidPDialog() {
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
