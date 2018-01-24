package com.interactivemedia.backpacker.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;

import java.nio.charset.Charset;

import static android.nfc.NdefRecord.createMime;
/**
*
* Will be used for adding a new friend via Nfc.
*/
public class AddFriendNfcActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback {


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_nfc);

        context = this;

        Log.e("NFC", "is in on onCreate");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //check if nfc is available
        if (nfcAdapter == null) {  //if that is the case we redirect to the add friend via email activity
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            //redirect to fallback activity to add friend via email
            Intent intent = new Intent(this, AddFriendEmailActivity.class);
            startActivity(intent);
            finish();
        } else {
            //check if NFC and Beam are enabled
            if (nfcAdapter.isEnabled()) {
                //This will refer back to createNdefMessage for what it will send
                nfcAdapter.setNdefPushMessageCallback(this, this);

                //This will be called if the message is sent successfully
                nfcAdapter.setOnNdefPushCompleteCallback(this, this);

            } else {
                //tell user to enable both and return to previous activity
                Toast.makeText(this, "Please enable NFC to proceed", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("NFC", "is in on resume");

        handleNfcIntent(getIntent());

    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.e("NFC", "onNewIntent");
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }


    /**
     * Parses the NDEF message from the intent.
     * @param intent Intent with the an Array and the information
     */
    private void handleNfcIntent(Intent intent) {

        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            //make request to api to exchange userIds
            String userId = Preferences.getUserId(this);
            if (userId != null) {

                // disable info text, because it is not relevant
                findViewById(R.id.text_view_nfc).setVisibility(View.INVISIBLE);

                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                        NfcAdapter.EXTRA_NDEF_MESSAGES);
                // only one message sent during the beam
                NdefMessage ndefMessage = (NdefMessage) rawMsgs[0];
                // record 0 contains the message, record 1 is the AAR, if present
                String friendId = new String(ndefMessage.getRecords()[0].getPayload());

                Log.e("NFC", friendId);

                //only make request, if user has internet
                //otherwise show toast and save id in preferences to upload later
                if(Request.hasInternetConnection(context)){
                    new AddFriendRelationship().execute("/users/" + userId + "/friends/" + friendId + "?notify=true");
                } else {
                    Preferences.saveFriendId(context, friendId);
                    Toast.makeText(this, "You do not have an internet connection. Don't worry! Your friend will be added later.", Toast.LENGTH_LONG).show();
                    finish();
                }

            } else {
                Toast.makeText(this, "You need to sign in", Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }
    }

    /**
     * Creates the NDEF message.
     * @param nfcEvent
     * @return new NdefMessage (text)
     */

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //create ndef message that contains, what we want to send to the other device
        String userId = Preferences.getUserId(this);
        return new NdefMessage(new NdefRecord[]{
                createMime("text/plain", userId.getBytes(Charset.forName("UTF-8"))),
                NdefRecord.createApplicationRecord(getPackageName())
        });
    }


    /**
     * After a successful push the user will get back to the home activity.
     * @param nfcEvent
     */
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        Preferences.saveNfcEvent(this, true);

        //we want to save the boolean in preferences to check it later in our firebase service
        //but only, if we we are online
        if(Request.hasInternetConnection(context)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "You have successfully exchanged locations", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "You do not have an internet connection. Don't worry! Your friend will be added later.", Toast.LENGTH_LONG).show();
                }
            });
        }

        //return to home activity
        finish();
    }

    /**
     * This function is called upon click on "Not working" button.
     * It redirects to another activity to add a friend via Email.
     *
     * @param view The button
     */
    public void openAlternative(View view) {
        Intent intent = new Intent(this, AddFriendEmailActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Both friends get each others locations by exchanging their user ids.
     *
     */
    @SuppressLint("StaticFieldLeak")
    private class AddFriendRelationship extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.put(context, strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(context, "There was an Error exchanging your locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(context, "You have successfully exchanged locations", Toast.LENGTH_LONG).show();
                //return to home activity
                finish();
            }
        }

    }
}
