package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.PictureDialogFragment;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.helpers.Storage;
import com.interactivemedia.backpacker.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity implements PictureDialogFragment.PictureDialogListener {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private ImageView imageView;
    private static final int IMAGE_CAPTURE_REQUEST = 2;
    private static final int IMAGE_STORAGE_REQUEST = 3;
    private static final int PERMISSION_READ_STORAGE_REQUEST = 4;
    private String currentPicturePath;
    private User user;
    private ProgressBar progressBar;
    private ConstraintLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editTextFirstName = findViewById(R.id.edit_text_first_name);
        editTextLastName = findViewById(R.id.edit_text_last_name);
        imageView = findViewById(R.id.image_view_profile_picture);
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);  //to hide it later


        new GetProfile().execute("/users/5a46519c6de6a50f3c46efba");     //TODO: use /users/me endpoint
    }


    /**
     * this function is called when a place was selected in place picker widget or a picture is taken
     *
     * @param requestCode integer we have set to differentiate between actions (camera, storage)
     * @param resultCode  integer that indicates the status code
     * @param data        data is needed to access the picture
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == IMAGE_CAPTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //get image from path, where it was saved
                Bitmap picture = BitmapFactory.decodeFile(currentPicturePath);
                //set picture of image view
                imageView.setImageBitmap(picture);
            }
        } else if (requestCode == IMAGE_STORAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //use storage class to get picture path
                String picturePath = Storage.getPicturePathFromStorage(data, this);
                if (picturePath != null) {
                    Bitmap picture = BitmapFactory.decodeFile(picturePath);
                    //set picture of image view
                    imageView.setImageBitmap(picture);
                }
            }
        }
    }

    /**
     * this function is called upon click of add picture button, opens option dialog (PictureDialogFragment)
     *
     * @param view the button
     */
    public void openDialog(View view) {
        PictureDialogFragment dialog = new PictureDialogFragment();
        dialog.show(getSupportFragmentManager(), "PictureDialogFragment");
    }


    /**
     * this function is called upon click on take picture via camera
     * it calls createImageFile() and opens the camera via an intent
     * <p>
     * The dialog fragment receives a reference to this Activity through the
     * Fragment.onAttach() callback, which it uses to call the following methods
     * defined by the PictureDialogFragment.PictureDialogListener interface
     */
    @Override
    public void onDialogCameraClick(PictureDialogFragment dialog) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //check if phone has a camera (intent can be resolved)
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = null;
            try {
                imageFile = Storage.createImageFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                currentPicturePath = imageFile.getAbsolutePath();
            } catch (IOException ex) {
                Log.e("Image", "Error creating file");
            }

            // Continue only if the File was successfully created
            if (imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.interactivemedia.backpacker.fileprovider",
                        imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);
            }

        }
    }

    /**
     * this function starts the check for permission, in the permission callback we will start the intent to pick an image
     */
    public void getPictureFromStorage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_STORAGE_REQUEST);
    }

    /**
     * this function is called, when the option storage is clicked in the picture options dialog
     * if the permission was already granted we call the function getPictureFromStorage, otherwise we ask for permssion
     */
    @Override
    public void onDialogStorageClick(PictureDialogFragment dialogFragment) {
        //get runtime permission for storage
        if (Storage.getStoragePermission(this, PERMISSION_READ_STORAGE_REQUEST)) {
            getPictureFromStorage();
        }
    }


    /**
     * this function is called after we ask for permission for access to external storage
     *
     * @param requestCode  is specified in the class
     * @param permissions  just one string in this case: storage
     * @param grantResults the result of the asking for permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STORAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    getPictureFromStorage();
                } else {
                    //TODO: handle permission denied
                    Toast.makeText(this, "Permission was denied. Use the camera option to upload pictures.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * this function starts the home activity, e.g. in case of error
     */
    private void redirectToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    /**
     * this function is called upon click of save profile button, it calls the async task to post the data
     *
     * @param view the button
     */
    public void saveProfile(View view) {
        //show progress bar
        progressBar.setVisibility(View.VISIBLE);
        //hide the rest
        contentLayout.setVisibility(View.GONE);
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String jsonBody = "{ \"firstName\": \"" + firstName + "\", \"lastName\": \"" + lastName + "\" }";
        Log.d("jsonbody", jsonBody);
        new PatchProfile().execute("/users/" + user.getId(), jsonBody);
    }

    private class PatchProfile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.patch(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            if (result.equals("error")) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getApplicationContext(), "There was an Error saving your profile", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Successfully saved your profile", Toast.LENGTH_LONG).show();
                //check, if there is a picture to upload
                if (currentPicturePath != null) {
                    new UploadPicture().execute("/users/" + user.getId() + "/avatar");
                } else {
                    //hide progress bar again
                    progressBar.setVisibility(View.GONE);
                    //redirect to HomeActivity
                    redirectToHome();
                }

            }
        }
    }

    /**
     * this async task makes an api call to get the logged in user's data
     */
    private class GetProfile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.get(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            if (result.equals("error")) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(getApplicationContext(), "There was an Error loading your profile", Toast.LENGTH_LONG).show();
                //redirect to home activity
                redirectToHome();

            } else {
                //we use the gson builder to add an exclusion strategy, which leads to gson excluding the field locations
                //this needs to be done, because the result contains only location ids, while the User class contains an ArrayList of Locations
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("locations");      //exclusion happening here!
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });
                Gson gson = gsonBuilder.create();
                user = gson.fromJson(result, User.class);
                if (user != null) {
                    //set the texts of the edit texts with the first and last name of user
                    editTextFirstName = findViewById(R.id.edit_text_first_name);
                    editTextLastName = findViewById(R.id.edit_text_last_name);

                    editTextFirstName.setText(user.getFirstName());
                    editTextLastName.setText(user.getLastName());

                    //check if the user has a profile picture, if yes show it (using glide library)
                    if(user.getAvatar() != null){
                        //we want to force glide to not use the cache to load the picture
                        //otherwise it might happen, that the old picture is loaded
                        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true);
                        Glide.with(getApplicationContext()).load(Request.DOMAIN_URL + user.getAvatar()).apply(requestOptions).into(imageView);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There was an Error loading your profile", Toast.LENGTH_LONG).show();
                    redirectToHome();
                }
            }

        }
    }

    private class UploadPicture extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            //to use the upload pictures function we need to turn picture string to array list containing it
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(currentPicturePath);
            return Request.uploadPictures(strings[0], arrayList, "PUT");
        }

        @Override
        protected void onPostExecute(String result) {
            //hide progress bar again
            progressBar.setVisibility(View.GONE);
            Log.d("JSON response: ", result);
            if (result.equals("error")) {
                Toast.makeText(getApplicationContext(), "There was an Error uploading your profile picture", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Profile picture uploaded successfully", Toast.LENGTH_LONG).show();
            }

            //redirect to HomeActivity
            redirectToHome();

        }
    }

}
