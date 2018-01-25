package com.interactivemedia.backpacker.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vali_ on 05.01.2018.
 * <p>
 * This class contains some helper functions to deal with the device's storage.
 * The functions are essential for activities and fragments, in which
 * images are taken via camera or chose from storage.
 */

public class Storage {
    /**
     * This function creates a file on the phone, where the image will be saved later.
     * See reference: https://developer.android.com/training/camera/photobasics.html#TaskPath
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


    /**
     * This function requests external storage permission, when the SDK of the phone
     * is 23 or above.
     *
     * @param activity    the activity, in which the function is called
     * @param requestCode request code needed to pass to requestPermissions function
     * @return true, if permission was granted, false otherwise
     */
    public static boolean getStoragePermission(Activity activity, int requestCode) {
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


    /**
     * This function retrieves the absolute path of an image file.
     *
     * @param data     the {@link Intent} holding the data
     * @param activity the activity, in which the function is called
     * @return absolute path of file as a String, null if an error occurred
     */
    public static String getPicturePathFromStorage(Intent data, Activity activity) {
        if (data.getData() != null) {
            Uri selectedImageUri = data.getData();
            //get path to selected image (always so fucking complicated in Android!)
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                return picturePath;
            } else {
                return null;
            }
        }
        return null;
    }
}
