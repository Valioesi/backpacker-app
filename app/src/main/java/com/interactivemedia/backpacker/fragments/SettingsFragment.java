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

    private TextView txt;

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


        txt = (TextView) view.findViewById(R.id.textView2);
        listView = (ExpandableListView) view.findViewById(R.id.lvexp_settings);
//        //initialize Data:
//        listDataHeader=new ArrayList<>();
//        listHash = new HashMap<String, List<String>>();
//
//
//        listDataHeader.add("How To Use The App");
//        listDataHeader.add("Credits");
//
//
//
//        List<String> howTo = new ArrayList<>();
//        howTo.add(getString(R.string.howTo1));
//
//
//        List<String> credits = new ArrayList<>();
//        credits.add(getString(R.string.credits));
//
//
//
//
//        //Adds Childs to Headings
//        listHash.put(listDataHeader.get(0), howTo);
//        listHash.put(listDataHeader.get(1), credits);
//
//
//
//        //Sets adapter
//        listAdapter = new ExpandableListSettingsAdapter(getContext(), listDataHeader, listHash);
//        listView.setAdapter(listAdapter);

      //  new DownloadXmlTask().execute(URL);


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


    //Implementation of AsyncTask used to download XML feed from stackoverflow.com
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            txt.setText(result);
        }
    }

    //Instantiates a StackOverflowXMLParser, creates variables for List of Entry Objects to hold the values;
    //Calls downloadURL() which fetches the feed and returns it as an InputStream
    //Uses StackOverflowXMLParser to parse the InputStream. StackOverflowParser populates a list of entries with data from the fead.
    //Processes the entries list and combines the feed data with HTML markup.
    //Returns an HTML string that is displays in the UI by the AsyncMethod onPOstExecute()

    private String loadXmlFromNetwork(String urlString) throws IOException {
        InputStream stream = null;

        //Instantiate the parser
        StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();
        List<Entry> entries = null;

        String title = null;
        String linkurl = null;
        String summary = null;

        StringBuilder builder = new StringBuilder();

        try {
            stream = downloadUrl(urlString);
            entries = stackOverflowXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }


        // StackOverflowXmlParser returns a List (called "entries") of Entry objects.
        // Each Entry object represents a single post in the XML feed.
        // This section processes the entries list to combine each entry with HTML markup.
        // Each entry is displayed in the UI as a link that optionally includes
        // a text summary.
        for (Entry entry : entries) {
            builder.append(entry.link);
            builder.append("\n" + entry.title + "\n");
            builder.append(entry.summary);
        }
        return builder.toString();

    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
    }




}