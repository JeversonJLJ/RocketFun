package com.jljsoluctions.rocketfun;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import java.io.File;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by jever on 02/07/2017.
 */

public class Util {
    private static final int MY_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 2;

    public static boolean checkWritePermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:com.jljsoluctions.rocketfun"));
                activity.startActivity(intent);
            }
        }
        return true;
    }

    public static boolean checkReadPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return true;
        else
            return false;

    }
}
