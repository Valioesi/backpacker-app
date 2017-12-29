package com.interactivemedia.backpacker.fragments;

import android.app.Dialog;
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
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_picture_option)
                .setItems(R.array.picture_options_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if(which == 0){ //storage
                            ((AddLocationActivity) getActivity()).handleClickStorage();
                        }else if(which == 1){ //camera
                            ((AddLocationActivity) getActivity()).handleClickCamera();
                        }
                    }
                });
        return builder.create();
    }
}