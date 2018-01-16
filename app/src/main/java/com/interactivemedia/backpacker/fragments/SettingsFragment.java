package com.interactivemedia.backpacker.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.EditProfileActivity;
import com.interactivemedia.backpacker.helpers.ExpandableListSettingsAdapter;
import com.interactivemedia.backpacker.helpers.StackOverflowXmlParser;
import com.interactivemedia.backpacker.helpers.Entry;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    private ExpandableListView listView;
    private ExpandableListSettingsAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    private static final String XML_URL = "http://stackoverflow.com/feeds/tag?tagnames=android&sort=newest";
    //XmlPullParser xpp=getResources().getXml(R.xml.user_documentation);


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


        listView = (ExpandableListView) view.findViewById(R.id.lvexp_settings);
        //initialize Data:
        listDataHeader=new ArrayList<>();
        listHash = new HashMap<String, List<String>>();


        listDataHeader.add("How To Use The App");
        listDataHeader.add("Credits");



        List<String> howTo = new ArrayList<>();
        howTo.add(getString(R.string.howTo1));


        List<String> credits = new ArrayList<>();
        credits.add(getString(R.string.credits));




        //Adds Childs to Headings
        listHash.put(listDataHeader.get(0), howTo);
        listHash.put(listDataHeader.get(1), credits);



        //Sets adapter
        listAdapter = new ExpandableListSettingsAdapter(getContext(), listDataHeader, listHash);
        listView.setAdapter(listAdapter);


        //create on click listener for open profile button
        Button button = view.findViewById(R.id.button_open_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile();
            }
        });
        return view;
    }

    /**
     * this function starts the EditProfileActivity
     */
    private void openProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }



}