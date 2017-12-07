package com.interactivemedia.backpacker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.interactivemedia.backpacker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyListFragment extends Fragment {

    private ListView lvMyLocation;


    public MyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment MyListFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static MyListFragment newInstance() {
        MyListFragment fragment = new MyListFragment();
        return fragment;
    }

    /**
     * Fills Parts of listitem_mylist.xml with dummy texts from a hardCoded String Array
     * Sources: https://stackoverflow.com/questions/28772909/listview-with-custom-adapter-in-fragment
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);

        CodeLearnAdpater locationListAdapter = new CodeLearnAdpater();
        lvMyLocation = (ListView) view.findViewById(R.id.lv_myloc);
        lvMyLocation.setAdapter(locationListAdapter);


        //String[] cities = new String[] {"Oper", "Kölner Dom", "Aussichtsplattform"};
        //String [] cities = new String [] {"Sydney", "Köln", "Jungfraujoch"};
        //String [] countries = new String [] {"Australien", "Deutschland", "Schweiz"};


        /*
        lvMyLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final String item = (String) adapterView.getItemAtPosition(position);
            }
        });
        */

        return view;


    }

    public void OnActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public class LocationChapter{
        String attraction;
        String city;
        String country;
    }

    String [] ListAttractions = new String []{"Oper", "Kölner Dom", "Aussichtsplattform"};
    String [] ListCity = new String []{"Sydney", "Köln", "Jungfraujoch"};
    String [] ListCountry = new String [] {"Australien", "Deutschland", "Schweiz"};

    public List<LocationChapter> getDataForListView(){
        List<LocationChapter> codeLocationChaptersList = new ArrayList<LocationChapter>();
        for (int i = 0; i< ListAttractions.length; i++){
            LocationChapter chapter = new LocationChapter();
            chapter.attraction = ListAttractions[i];
            chapter.city = ListCity[i];
            chapter.country=ListCountry[i];
            codeLocationChaptersList.add(chapter);
        }
        return codeLocationChaptersList;
    }


    public class CodeLearnAdpater extends BaseAdapter{
        List <LocationChapter> codeLearnChapterList;
        public CodeLearnAdpater() {
            codeLearnChapterList = getDataForListView();
        }


        @Override
        public int getCount(){
            return codeLearnChapterList.size();
        }

        @Override
        public LocationChapter getItem(int arg0){
            return codeLearnChapterList.get(arg0);
        }

        @Override
        public long getItemId(int arg0){
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2){
            if (arg1==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.listitem_mylist, arg2,false);
            }

            TextView chapterCity = (TextView)arg1.findViewById(R.id.tv_cityName);
            TextView chapterLocation = (TextView)arg1.findViewById(R.id.tv_locationName);
            TextView chapterCountry = (TextView)arg1.findViewById(R.id.tv_countryName);

            LocationChapter chapter = codeLearnChapterList.get(arg0);

            String attraction = chapter.attraction;
            String city = chapter.city;
            String country=chapter.country;

            chapterCity.setText(chapter.city);
            chapterLocation.setText(chapter.attraction);
            chapterCountry.setText(chapter.country);

            return arg1;
        }
    }

}
