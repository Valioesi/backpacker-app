package com.interactivemedia.backpacker.activities;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;

import java.nio.charset.Charset;

import static android.nfc.NdefRecord.createMime;

public class AddFriendNfcActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback{

    private NfcAdapter nfcAdapter;
    private String stepFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_nfc);

        Log.e("NFC", "is in on onCreate");
        //flag is needed to check on receiver if we need to send our user id back
        stepFlag = "first";
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //check if nfc is available
        if (nfcAdapter == null) {  //if that is the case we return to the previous activity --> later implement fallback
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            //redirect to fallback activity to add friend via email
            Intent intent = new Intent(this, AddFriendEmailActivity.class);
            startActivity(intent);
            finish();
        } else {
            //This will refer back to createNdefMessage for what it will send
            nfcAdapter.setNdefPushMessage(buildMessage(), this);

            //This will be called if the message is sent successfully
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);

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
     * Parses the NDEF Message from the intent
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
                Toast.makeText(this, friendId, Toast.LENGTH_LONG).show();
                String flag = new String(ndefMessage.getRecords()[1].getPayload());

                Log.e("NFC", friendId);
                //invoke beam to send the id of receiver to phone of initiator of nfc
                //only send it back, if flag is set to first
                if (flag.equals("first")) {
                    stepFlag = "second";
                    //This will refer back to buildMessage for what it will send
                    nfcAdapter.setNdefPushMessage(buildMessage(), this);
                    //This will be called if the message is sent successfully
                    nfcAdapter.setOnNdefPushCompleteCallback(this, this);
                    Log.e("NFC", "before invoke beam");
                    nfcAdapter.invokeBeam(this);
                }


                new AddFriendRelationship().execute("/users/" + userId + "/friends/" + friendId);
            } else {
                Toast.makeText(getApplicationContext(), "You need to sign in", Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }
    }


    public NdefMessage buildMessage(){
        Log.d("NFC", "Is in build message");
        //create ndef message that contains, what we want to send to the other device
        String userId = Preferences.getUserId(this);
        NdefMessage message = new NdefMessage(new NdefRecord[]{
                createMime("text/plain", userId.getBytes(Charset.forName("UTF-8"))),
                createMime("text/plain", stepFlag.getBytes()),
                NdefRecord.createApplicationRecord(getPackageName())
        });
        return message;
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //disable beam
                nfcAdapter.setNdefPushMessage(null, activity);
                nfcAdapter.disableForegroundDispatch(activity);
            }
        });

        Log.e("NFC", "Push complete");
        //return to home activity
    }


    private class AddFriendRelationship extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Request.put(getApplicationContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error exchanging your locations", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")){
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }else {
                Log.d("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "You have successfully exchanged locations", Toast.LENGTH_LONG).show();
                //return to home activity
            /*    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();*/
            }
        }

    }
}
