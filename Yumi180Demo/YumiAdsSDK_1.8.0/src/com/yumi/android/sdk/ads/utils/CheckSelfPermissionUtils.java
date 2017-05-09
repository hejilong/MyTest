package com.yumi.android.sdk.ads.utils;

import java.util.ArrayList;
import java.util.List;

import com.yumi.android.sdk.ads.publish.YumiCheckPermission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 6.0系统权限检查
 * @author Administrator
 *
 */
public class CheckSelfPermissionUtils {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    /**
     * 检查是否有涉及到的用户隐私权限
     * @param context
     */
    public static void CheckSelfPermissionYumi(Activity context) {
        try {
            if(!YumiCheckPermission.isCheckPermission()) //是否检查权限开关
            {
                return;
            }
            if (android.os.Build.VERSION.SDK_INT < 23) {
                return;
            }
            List<String> denyPermissions = findDeniedPermissions(context, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (denyPermissions != null && denyPermissions.size() > 0) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(context, (String[]) denyPermissions.toArray(new String[denyPermissions.size()]), MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }

        } catch (Exception e) {
        }
    }

    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        try {
            List<String> denyPermissions = new ArrayList<>();
            for (String value : permission) {
                if (ContextCompat.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                    denyPermissions.add(value);
                }
            }
            return denyPermissions;
        } catch (Exception e) {
        }
        return null;
    }
}
