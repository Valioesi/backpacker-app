package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.MultiSelectionSpinner;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * this activity deals with the adding of a location to the user's list
 * it uses place picker to select a location
 */
public class AddLocationActivity extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int IMAGE_CAPTURE_REQUEST = 2;
    private static final String[] OPTIONS_CATEGORIES = {"Bar", "Restaurant", "Beach", "Club"};
    private Place place;
    private String[] selectedCategories;
    private ArrayList<String> picturePaths;
    String currentPicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        picturePaths = new ArrayList<>();

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
     * @param requestCode integer we have set to differentiate between actions (placepicker, camera)
     * @param resultCode  integer that indicates the status code
     * @param data        data is needed to access the selected place via getPlace or the picture taken
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                TextView textView = findViewById(R.id.text_view_location);
                textView.setText(place.getName());
                //find spinner and initialize it
                MultiSelectionSpinner spinner = findViewById(R.id.spinner);
                spinner.setItems(OPTIONS_CATEGORIES);
                spinner.setListener(this);
                Log.d("place picker", place.getAddress().toString());
            }
        } else if (requestCode == IMAGE_CAPTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //create new image view, add it to layout and set the bitmap
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 200);
                //layoutParams.setMarginEnd(5);
                // layoutParams.gravity = Gravity.LEFT;
                layoutParams.setMargins(0, 0, 10, 0);
                imageView.setLayoutParams(layoutParams);
                //get image from path, where it was saved
                Bitmap picture = BitmapFactory.decodeFile(currentPicturePath);
                imageView.setImageBitmap(picture);
                LinearLayout layout = findViewById(R.id.image_layout);
                layout.addView(imageView);
                //add current file path to array
                picturePaths.add(currentPicturePath);
            }
        }
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {
        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
        //transform list to string array and save it in selectedCategories
        selectedCategories = strings.toArray(new String[0]);
    }

    /**
     * this function is called on click of add picture button,
     * it calls createImageFile() and opens the camera via an intent
     *
     * @param view the button
     */
    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //check if phone has a camera (intent can be resolved)
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File imageFile = null;
            try {
                imageFile = createImageFile();
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
     * this function creates a file on the phone, where the image will be saved later
     * taken from: https://developer.android.com/training/camera/photobasics.html#TaskPath
     *
     * @return the created file
     * @throws IOException if file cannot be created
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPicturePath = image.getAbsolutePath();
        return image;
    }

    /**
     * this function is called upon click of save location button
     * it starts the AsyncTask to make post request to server
     *
     * @param view the button
     */
    public void saveLocation(View view) {
        //use geocoder to get city and country of our place
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
            String city = addresses.get(0).getLocality() != null ? addresses.get(0).getLocality() : "No city";
            String country = addresses.get(0).getCountryName() != null ? addresses.get(0).getCountryName() : "No country";
            Log.d("address", addresses.get(0).toString());

            //create location object to parse it via gson
            EditText editText = findViewById(R.id.description);
            String description = editText.getText().toString();
            Location location = new Location(
                    place.getId(),
                    "5a32abc2bc49d32e6cddb815",     //TODO: our user id,
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
        }catch (IOException e){
            e.printStackTrace();
        }


    }

      private class PostLocation extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            return Request.post(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            if(result.equals("error")){
                Toast.makeText(getApplicationContext(), "There was an Error saving the location", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Location saved successfully", Toast.LENGTH_LONG).show();
            }

        }
    }

}
