package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.fragments.PictureDialogFragment;
import com.interactivemedia.backpacker.adapters.MultiSelectionSpinner;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Storage;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this activity deals with the adding of a location to the user's list
 * it uses place picker to select a location
 */
public class AddLocationActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener, PictureDialogFragment.PictureDialogListener {

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int IMAGE_CAPTURE_REQUEST = 2;
    private static final int IMAGE_STORAGE_REQUEST = 3;
    private static final int PERMISSION_READ_STORAGE_REQUEST = 4;
    private String[] optionsCategories;
    private Place place;
    private String[] selectedCategories;
    private ArrayList<String> picturePaths; //important, holds all paths of selected images -> needed for upload later
    private String currentPicturePath;
    private ProgressBar progressBar;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        //get progress bar, we want to make it visible later
        progressBar = findViewById(R.id.progress_bar);
        //get layout to hide it later
        layout = findViewById(R.id.add_location_layout);

        picturePaths = new ArrayList<>();

        optionsCategories = getResources().getStringArray(R.array.categories);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Exception", "Google Play Services not available");
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Exception", "Google Play services repairable exception");
        }
    }

    /**
     * this function is called when a place was selected in place picker widget or a picture is taken
     *
     * @param requestCode integer we have set to differentiate between actions (placepicker, camera, storage)
     * @param resultCode  integer that indicates the status code
     * @param data        data is needed to access the selected place via getPlace or the picture
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                TextView textView = findViewById(R.id.text_view_location);
                textView.setText(place.getName());
                //find spinner and initialize it
                MultiSelectionSpinner spinner = findViewById(R.id.spinner);
                spinner.setItems(optionsCategories);
                spinner.setListener(this);
                Log.d("place picker", place.getAddress().toString());
            } else if(resultCode == RESULT_CANCELED){
                //direct back, if user quits picker
                finish();
            }
        } else if (requestCode == IMAGE_CAPTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //get image from path, where it was saved
                Bitmap picture = BitmapFactory.decodeFile(currentPicturePath);
                //add the picture to the Activity's view
                createImageView(picture);
                //add current file path to array
                picturePaths.add(currentPicturePath);
            }
        } else if (requestCode == IMAGE_STORAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //use storage class to get picture path
                String picturePath = Storage.getPicturePathFromStorage(data, this);
                if (picturePath != null) {
                    Bitmap picture = BitmapFactory.decodeFile(picturePath);
                    //add the picture to the Activity's view
                    createImageView(picture);
                    //add current file path to array
                    picturePaths.add(picturePath);

                }
            }
        }
    }


    /**
     * this function creates a new ImageView, adds it to the layout and sets the given Bitmap as Image
     *
     * @param picture Bitmap, either from camera or from storage
     */
    public void createImageView(Bitmap picture) {
        //create new image view, add it to layout and set the bitmap
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 200);
        layoutParams.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(picture);
        LinearLayout layout = findViewById(R.id.image_layout);
        layout.addView(imageView);
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {
        //transform list to string array and save it in selectedCategories
        selectedCategories = strings.toArray(new String[0]);
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
     * this function is called upon click of add picture button, opens option dialog (PictureDialogFragment)
     *
     * @param view the button
     */
    public void openDialog(View view) {
        PictureDialogFragment dialog = new PictureDialogFragment();
        dialog.show(getSupportFragmentManager(), "PictureDialogFragment");
    }

    /**
     * this function is called upon click of save location button
     * it starts the AsyncTask to make post request to server
     *
     * @param view the button
     */
    public void saveLocation(View view) {
        EditText editText = findViewById(R.id.description);
        String description = editText.getText().toString();
        //only proceed, if the user put in a description
        if (!description.equals("")) {
            //show progress bar
            progressBar.setVisibility(View.VISIBLE);
            //hide rest
            layout.setVisibility(View.GONE);
            //use geocoder to get city and country of our place
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                String city = addresses.get(0).getLocality() != null ? addresses.get(0).getLocality() : "No city";
                String country = addresses.get(0).getCountryName() != null ? addresses.get(0).getCountryName() : "No country";
                Log.d("address", addresses.get(0).toString());
                //create location object to parse it via gson
                String userId = Preferences.getUserId(getApplicationContext());
                Location location = new Location(
                        place.getId(),
                        userId,
                        place.getName().toString(),
                        true,
                        description,
                        selectedCategories,
                        new double[]{place.getLatLng().latitude, place.getLatLng().longitude},
                        city,
                        country
                );
                //transform to json via gson
                Gson gson = new Gson();
                String locationJson = gson.toJson(location);
                //call AsyncTask
                new PostLocation().execute("/locations", locationJson);
                Log.d("JSON body", locationJson);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please add a description", Toast.LENGTH_LONG).show();
        }

    }


    private class PostLocation extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.post(getApplicationContext(), strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error saving the location", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")){
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Location saved successfully", Toast.LENGTH_LONG).show();
                //get id of newly created location so that we can do a request to upload the pictures
                Gson gson = new Gson();
                Location location = gson.fromJson(result, Location.class);
                if (location != null) {
                    //check, if there are images to upload
                    if (picturePaths.size() > 0) {
                        new UploadPictures().execute("/locations/" + location.get_id() + "/images");
                    } else {
                        //return to to home activity
                        finish();
                    }
                }
            }

        }
    }

    private class UploadPictures extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.uploadPictures(getApplicationContext(), strings[0], picturePaths, "POST");
        }

        @Override
        protected void onPostExecute(String result) {
            //hide progress bar again
            progressBar.setVisibility(View.GONE);
            if (result == null) {
                Toast.makeText(getApplicationContext(), "There was an Error uploading the location's pictures", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")){
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);
                Toast.makeText(getApplicationContext(), "Pictures uploaded successfully", Toast.LENGTH_LONG).show();
            }

            //return to previous activity
            finish();

        }
    }


}


