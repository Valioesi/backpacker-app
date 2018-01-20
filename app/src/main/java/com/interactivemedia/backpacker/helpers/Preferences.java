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
     * @return id token as a string if available, otherwise null
     */
    public static String getIdToken(Context context){
        return getPreferences(context).getString(context.getString(R.string.saved_token), null);
    }

    /**
     * this function gets the user id from the shared preferences
     * @param context application context, needed to call appropriate functions
     * @return user id (google id) as string if available, otherwise null
     */
    public static String getUserId(Context context){
        return getPreferences(context).getString(context.getString(R.string.saved_id), null);
    }


    /**
     * This function gets the earlier saved string from the shared preferences
     * @param context application context, needed to call appropriate functions
     * @return true (nfc) or false (if it was not via nfc)
     */
    public static boolean getNfcEvent(Context context){
        return getPreferences(context).getBoolean(context.getString(R.string.saved_nfc_event), false);
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
     * this function saves the user id (received as result of post /users) in the SharedPreference
     *
     * @param context application context, needed to call appropriate functions
     * @param id id as a String
     */
    public static void saveUserId(Context context, String id) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.saved_id), id);
        editor.apply();
        //this will log the id saved in shared preferences
        Log.i("Preferences", sharedPreferences.getString(context.getString(R.string.saved_id), "id not present"));
    }

    /**
     * This function saves a string to preferences to indicate, if it the adding of a friend was via NFC or Email.
     *
     * @param context application context, needed to call appropriate functions
     * @param nfcEvent This boolean will be passed to indicate, if it the adding of a friend was via NFC (true).
     *
     */
    public static void saveNfcEvent(Context context, boolean nfcEvent){
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.saved_nfc_event), nfcEvent);
        editor.apply();
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
