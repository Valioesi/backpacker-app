package com.interactivemedia.backpacker.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.EditProfileActivity;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.helpers.Preferences;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * A simple {@link Fragment} subclass.
 * This fragment has the smallest functionality of the 4 "main fragments".
 * It only holds 3 buttons. "Edit Profile" leads to {@link EditProfileActivity},
 * "Show Credits" shows a popup window, which shows info about the dev team of the application.
 * "Logout" logs the user out and redirects to {@link LoginActivity}.
 */
public class SettingsFragment extends Fragment {

    private Button btn_openProfile;
    private Button btn_credits;
    private Button btn_logout;

    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        context = getContext();

        //create on click listener for open profile button
        btn_openProfile = view.findViewById(R.id.button_open_profile);
        btn_openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });

        //create on click listener for showing the credits
        btn_credits=view.findViewById(R.id.button_show_credits);
        btn_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCredits();
            }
        });

        //create on click listener for Logout Button
        btn_logout=view.findViewById(R.id.button_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage();
            }
        });


        //Re-initialize the Client for signing out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);




        return view;
    }

    /**
     * Shows a dialog to confirm logout.
     */
    private void alertMessage() {
        //remove friend confirmation dialog
        //taken from: http://www.androidhub4you.com/2012/09/alert-dialog-box-or-confirmation-box-in.html
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            logout();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // No button clicked
                            // do nothing
                            break;
                    }
                }
            };

            //Show "warning" Dialog, if user is sure about deleting friend.
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to log out from this app?")
                    .setTitle("Logout")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }


    /**
     * This function logs the user out.
     */
    private void logout() {


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        //start home activity, when account is not null (user already signed in)
        if (account != null) {
            Log.d("LOGOUT", "Google Accoutn is not null");
            if (mGoogleSignInClient != null) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("LogoutButton", "You were Logged out succesfully");
                                //remove user id from preferences to indicate, that he is not logged in
                                Preferences.saveUserId(context, null);

                                //remove FCM token
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            FirebaseInstanceId.getInstance().deleteInstanceId();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();

                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                //remove home activity from stack
                                getActivity().finish();
                            }
                        });
            }
        }
    }

    /**
     * This function opens a pop up to show the credits.
     */
    private void showCredits() {
        //show credit information
        //source: https://stackoverflow.com/questions/6264694/how-to-add-message-box-with-ok-button
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(R.string.credits);
        dlgAlert.setTitle("Credits");
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //for dismissing the dialog there is no action necessary
            }
            });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    /**
     * This function starts the {@link EditProfileActivity}.
     */
    private void openProfile() {
        Intent intent = new Intent(context, EditProfileActivity.class);
        startActivity(intent);
    }



}