package com.interactivemedia.backpacker.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddLocationActivity;

/**
 * this DialogFragment handles the dialog in which the user can chose the option of adding an image (camera or storage)
 * used in AddLocationFragment
 */
public class PictureDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it.
    *
    * Vali: we do this, so that we can use this fragment in multiple activities (EditProfile + AddLocation)
    * */
    public interface PictureDialogListener {
        void onDialogStorageClick(PictureDialogFragment dialog);
        void onDialogCameraClick(PictureDialogFragment dialog);
    }


    // Use this instance of the interface to deliver action events
    PictureDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PictureDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_picture_option)
                .setItems(R.array.picture_options_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which == 0){ //storage
                            mListener.onDialogStorageClick(PictureDialogFragment.this);
                        }else if(which == 1){ //camera
                            mListener.onDialogCameraClick(PictureDialogFragment.this);
                        }
                    }
                });
        return builder.create();
    }



}