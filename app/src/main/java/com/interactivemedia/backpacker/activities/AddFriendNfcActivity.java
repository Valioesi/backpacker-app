package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.interactivemedia.backpacker.AddFriendEmailActivity;
import com.interactivemedia.backpacker.R;

import static android.nfc.NdefRecord.createMime;

public class AddFriendNfcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_nfc);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //check if nfc is available
        if (nfcAdapter == null) {  //if that is the case we return to the previous activity --> later implement fallback
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            //redirect to fallback activity to add friend via email
            Intent intent = new Intent(this, AddFriendEmailActivity.class);
            startActivity(intent);
        } else {
            //create ndef message that contains, what we want to send to the other device
            String text = "this is the text, we want to send";
            NdefMessage message = new NdefMessage(
                    new NdefRecord[]{createMime(
                            "application/vnd.com.interactivemedia.backpacker", text.getBytes())
                    });
            nfcAdapter.setNdefPushMessage(message, this, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        Log.d("NFC message", new String(msg.getRecords()[0].getPayload()));
    }



}
