package com.interactivemedia.backpacker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Request;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editTextFirstName = findViewById(R.id.edit_text_first_name);
        editTextLastName = findViewById(R.id.edit_text_last_name);

        new GetProfile().execute("/users/5a46519c6de6a50f3c46efba");     //TODO: use /users/me endpoint
    }

    /**
     * this function is called upon click of save profile button, it calls the async task to post the data
     *
     * @param view the button
     */
    public void saveProfile(View view) {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String jsonBody = "{ \"firstName\": \"" + firstName + "\", \"lastName\": \"" + lastName + "\" }";
        Log.d("jsonbody", jsonBody);
        new PatchProfile().execute("/users/5a46519c6de6a50f3c46efba", jsonBody);
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
                //redirect to HomeActivity
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            } else {
                //use json object instead of gson, because we have to ignore locations
                try {
                    JSONObject user = new JSONObject(result);
                    //set the texts of the edit texts with the first and last name of user
                    editTextFirstName = findViewById(R.id.edit_text_first_name);
                    editTextLastName = findViewById(R.id.edit_text_last_name);

                    editTextFirstName.setText(user.getString("firstName"));
                    editTextLastName.setText(user.getString("lastName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
