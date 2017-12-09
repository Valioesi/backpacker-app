package com.interactivemedia.backpacker.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.interactivemedia.backpacker.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MyListFragment extends Fragment{

    private ListView lvMyLocation;
    //private ToggleButton toggleFavorite;
    private ImageButton imageButton;
    private View view;


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
        return new MyListFragment();
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
        view = inflater.inflate(R.layout.fragment_my_list, container, false);

        fillListAdapter(view);


//         toggleFavorite=(ToggleButton) view.findViewById(R.id.toggleFavorite);
//        toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    //Toggle is enabled
//                    Log.i("Toggle", "Enabled");
//                } else {
//                    //Toggle is disabled
//                    Log.i ("Toggle", "Disabled");
//                }
//            }
//        });

        return view;
    }


    private void fillListAdapter(View view) {
        CodeLearnAdpater locationListAdapter = new CodeLearnAdpater();
        lvMyLocation = view.findViewById(R.id.lv_myloc);
        lvMyLocation.setAdapter(locationListAdapter);
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
        List<LocationChapter> codeLocationChaptersList = new ArrayList<>();
        for (int i = 0; i< ListAttractions.length; i++){
            LocationChapter chapter = new LocationChapter();
            chapter.attraction = ListAttractions[i];
            chapter.city = ListCity[i];
            chapter.country=ListCountry[i];
            codeLocationChaptersList.add(chapter);
        }
        return codeLocationChaptersList;
    }

    static class ViewHolder{
        TextView nameLocation;
        TextView nameCity;
        TextView nameCountry;
        ImageButton btn_favorite;
    }

    public class CodeLearnAdpater extends BaseAdapter{
        List <LocationChapter> codeLearnChapterList;
        CodeLearnAdpater() {
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
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder = new ViewHolder();
            final boolean isFavorite=false;

            if (convertView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem_mylist, parent, false);

                viewHolder.nameLocation=(TextView) convertView.findViewById(R.id.tv_locationName);
                viewHolder.nameCountry=(TextView) convertView.findViewById(R.id.tv_countryName);
                viewHolder.nameCity= (TextView) convertView.findViewById(R.id.tv_cityName);
                viewHolder.btn_favorite=(ImageButton) convertView.findViewById(R.id.btn_favorite);

                convertView.setTag(viewHolder);
            } else viewHolder = (ViewHolder) convertView.getTag();

            LocationChapter chapter = codeLearnChapterList.get(position);

            String attraction = chapter.attraction;
            String city = chapter.city;
            String country=chapter.country;

            viewHolder.nameLocation.setText(attraction);
            viewHolder.nameCity.setText(city);
            viewHolder.nameCountry.setText(country);

            viewHolder.btn_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i ("ONCLICKLISTENER", "IRGENDWAS");
                    //viewHolder.btn_favorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Log.i("OnClick", "IsFavorite");
                }
            });

            return convertView;
        }

    }

}
