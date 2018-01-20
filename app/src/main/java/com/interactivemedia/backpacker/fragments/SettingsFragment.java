package com.interactivemedia.backpacker.fragments;

import android.app.AlertDialog;
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
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.EditProfileActivity;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.helpers.Preferences;

import java.nio.channels.SocketChannel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button btn_openProfile;
    private Button btn_credits;
    private Button btn_logout;

    private GoogleSignInClient mGoogleSignInClient;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);




        return view;
    }

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to log out from this app?")
                    .setTitle("Logout")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }


    private void logout() {


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
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
                                Preferences.saveUserId(getContext(), null);

                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                //remove home activity from stack
                                getActivity().finish();
                            }
                        });
            }
        }
    }

    private void showCredits() {
        //show credit information
        //source: https://stackoverflow.com/questions/6264694/how-to-add-message-box-with-ok-button
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
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
     * this function starts the EditProfileActivity
     */
    private void openProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }



}