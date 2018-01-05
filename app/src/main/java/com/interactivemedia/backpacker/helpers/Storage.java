package com.interactivemedia.backpacker.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vali_ on 05.01.2018.
 *
 * this class contains some helper functions to deal with storage stuff, e.g. create an image file on the phone
 * this class was created to reuse code in AddLocationActivity and EditProfileActivity
 */

public class Storage {
    /**
     * this function creates a file on the phone, where the image will be saved later
     * taken from: https://developer.android.com/training/camera/photobasics.html#TaskPath
     *
     * @return the created file
     * @throws IOException if file cannot be created
     */
    public static File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public static boolean getStoragePermission(Activity activity, int requestCode){
        if (Build.VERSION.SDK_INT >= 23 && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.d("Permission", "Needs an explanation");
            } else {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            }
            return false;
        } else {
            return true;
        }

    }

}
