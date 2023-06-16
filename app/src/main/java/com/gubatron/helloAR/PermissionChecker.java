package com.gubatron.helloAR;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

public class PermissionChecker {
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    public static void checkCameraPermissions(Activity activity) {
        // Check if the Camera permission has been granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // If not, request it
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }
}
