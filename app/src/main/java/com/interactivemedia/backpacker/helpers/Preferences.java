package com.interactivemedia.backpacker.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.interactivemedia.backpacker.R;

/**
 * this class shell provide some helper functions to deal with saving and reading from shared preferences
 * Created by vali_ on 12.01.2018.
 */

public class Preferences {

    /**
     * this function gets the id token from the shared preferences
     * @param context application context, needed to call appropriate functions
     * @return id token as a string
     */
    public static String getIdToken(Context context){
        return getPreferences(context).getString(context.getString(R.string.saved_token), "key not present");
    }

    /**
     * this function saves the id token (taken from our GoogleSignInAccount in the SharedPreference
     *
     * @param context application context, needed to call appropriate functions
     * @param token token as a String, taken from account
     */
    public static void saveIdToken(Context context, String token) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.saved_token), token);
        editor.apply();
        //this will log the token saved in shared preferences
        Log.i("Preferences", sharedPreferences.getString(context.getString(R.string.saved_token), "key not present"));
    }

    /**
     * helper function to get the shared preferences
     * @param context application context, needed to call appropriate functions
     * @return shared preferences
     */
    private static SharedPreferences getPreferences(Context context){
         return context.getSharedPreferences(context.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
    }

}
