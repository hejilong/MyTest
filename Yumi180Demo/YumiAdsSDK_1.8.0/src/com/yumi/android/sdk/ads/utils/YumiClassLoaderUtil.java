package com.yumi.android.sdk.ads.utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import dalvik.system.DexClassLoader;

/**
 * 类加载器工具类
 * @author hjl
 *
 */
public class YumiClassLoaderUtil {
    protected static final boolean onoff = true;

    protected static String TAG = "YumiClassLoaderUtil";

    /**
     *  Adapter加载器
     * @param activity
     * @param fileName 要加载的适配器文件名称
     * @return
     */
    public static DexClassLoader AdapterDexLoader(Activity activity, String fileName) {
        try {

            File dir = activity.getDir("dex", Context.MODE_PRIVATE);
            File soFile = new File(dir, fileName);
            if (!soFile.exists()) {
                int result = FileUtils.assetToFile(activity, fileName, soFile);
                ZplayDebug.d(TAG, "AdapterDexLoader assetToFile  result: " + result, onoff);
            }
            final File optimizedDexOutputPath = activity.getDir("temp", Context.MODE_PRIVATE);
            DexClassLoader classLoader = new DexClassLoader(soFile.getAbsolutePath(), optimizedDexOutputPath.getAbsolutePath(), null, activity.getClassLoader());
            return classLoader;
        } catch (Exception e) {
            e.printStackTrace();
            ZplayDebug.e(TAG, "AdapterDexLoader error : ", e, onoff);
        }
        return null;
    }

    /**
     * Apk类加载器
     * @param activity
     * @param paramClassLoader
     */
    @SuppressWarnings("unused")
    public static void setApkClassLoader(Activity activity, ClassLoader paramClassLoader) {
        try {
            Class localClass = Class.forName("android.app.ActivityThread");
            Object localObject1 = localClass.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0]);
            Field localField1 = localClass.getDeclaredField("mPackages");
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localObject1);
            localField1.setAccessible(false);
            Method localMethod = localObject2.getClass().getMethod("get", new Class[] { Object.class });
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = activity.getPackageName();
            Object localObject3 = ((WeakReference) localMethod.invoke(localObject2, arrayOfObject)).get();
            Field localField2 = localObject3.getClass().getDeclaredField("mClassLoader");
            localField2.setAccessible(true);
            localField2.set(localObject3, paramClassLoader);
            localField2.setAccessible(false);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
            ZplayDebug.e(TAG, "setApkClassLoader error : ", e, onoff);
        }
    }
}
