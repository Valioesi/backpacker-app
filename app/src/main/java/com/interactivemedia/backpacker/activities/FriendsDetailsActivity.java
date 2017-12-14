package com.interactivemedia.backpacker.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.interactivemedia.backpacker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FriendsDetailsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);
        //find Spinner
        Spinner spnCountry = findViewById(R.id.SpinnerCountry);

        //get list of countrys for spnCountry
        //taken from: https://stackoverflow.com/questions/22121253/where-to-get-list-of-countries-for-spinner-in-android
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //assign ArrayAdapter to country list
        spnCountry.setAdapter(countryAdapter);


        //find ListView
        ListView lvfavplaces = findViewById(R.id.lvFavoritplaces);

        //create dummy-list
        List<String> testList = new ArrayList<String>();
        testList.add("Wasserfall");
        testList.add("Tropfsteinhöhle");
        //create ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                testList);
        //assign ArrayAdapter to friends list
        lvfavplaces.setAdapter(arrayAdapter);

        //create "remove friend" button with onClickListener
        Button btn = (Button) findViewById(R.id.btnRemovefriend);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alertMessage();
            }
        });

    }

    //remove friend confirmation dialog
    //taken from: http://www.androidhub4you.com/2012/09/alert-dialog-box-or-confirmation-box-in.html
    public void alertMessage() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        // Toast.makeText(FriendsDetailsActivity.this, "Yes Clicked",
                        //        Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        // Toast.makeText(FriendsDetailsActivity.this, "No Clicked",
                        //        Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
