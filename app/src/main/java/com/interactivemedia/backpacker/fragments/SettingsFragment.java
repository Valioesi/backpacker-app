package com.interactivemedia.backpacker.fragments;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.ExpandableListSettingsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;





/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends Fragment {


    private ExpandableListView listView;
    private ExpandableListSettingsAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;




    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
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




        listView = (ExpandableListView)view.findViewById(R.id.lvexp_settings);

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
        return view;
    }



}
