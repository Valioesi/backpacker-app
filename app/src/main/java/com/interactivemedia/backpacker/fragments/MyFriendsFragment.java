package com.interactivemedia.backpacker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddFriendNfcActivity;
import com.interactivemedia.backpacker.activities.FriendDetailsActivity;
import com.interactivemedia.backpacker.activities.LoginActivity;
import com.interactivemedia.backpacker.adapters.FillMyFriendsListAdapter;
import com.interactivemedia.backpacker.helpers.Preferences;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * The user will be shown a list of his friends including names and profile pictures.
 * On click of an item {@link FriendDetailsActivity} will be opened. A {@link FloatingActionButton}
 * will direct toe {@link AddFriendNfcActivity}. This fragment will only be used inside {@link com.interactivemedia.backpacker.activities.HomeActivity}
 */
public class MyFriendsFragment extends Fragment {

    private ArrayList<User> myFriends;
    private FillMyFriendsListAdapter fillMyFriendsListAdapter;

    private TextView tv_noFriends;
    private ListView lvfriends;
    private Context context;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MyFriendsFragment.
     */
    public static MyFriendsFragment newInstance() {
        return new MyFriendsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        context = getContext();

        //find ListView
        lvfriends = view.findViewById(R.id.listViewFriends);

        //find TextView for showing if there are no friends
        tv_noFriends = view.findViewById(R.id.noFriends);

        myFriends = new ArrayList<>();

        //create adapter containing friends list
        fillMyFriendsListAdapter = new FillMyFriendsListAdapter(getContext(), R.layout.listitem_friends, myFriends);
        //assign ArrayAdapter to friends list
        lvfriends.setAdapter(fillMyFriendsListAdapter);

        lvfriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = myFriends.get(i);

                Intent intent = new Intent(getContext(), FriendDetailsActivity.class);
                intent.putExtra("userId", user.getId());
                intent.putExtra("firstName", user.getFirstName());
                intent.putExtra("lastName", user.getLastName());
                intent.putExtra("avatar", user.getAvatar());
                startActivity(intent);
            }
        });


        //set on click listener for add friend button -> open AddFriendNfcActivity
        FloatingActionButton button = view.findViewById(R.id.add_friend_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendNfcActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    //use the onResume method to load for first time or reload friends (e.g. after one has been added)
    @Override
    public void onResume() {
        super.onResume();
        //check, if user is online
        if (Request.hasInternetConnection(context)) {
            //load friends by calling AsyncTask
            loadFriends();
        } else {
            //show sad backpack
            View view = getView();
            if (view != null) {
                getView().findViewById(R.id.main_layout).setVisibility(View.GONE);
                getView().findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.add_friend_info_offline).setVisibility(View.VISIBLE);
            }

        }

    }

    /**
     * This function calls {@link GetFriends} to load friends and their locations.
     */
    private void loadFriends() {
        String userId = Preferences.getUserId(context);
        new GetFriends().execute("/users/" + userId + "/friends");
    }


    /**
     * This {@link AsyncTask} performs a request to the API to load friends.
     */
    @SuppressLint("StaticFieldLeak")
    private class GetFriends extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("DoInBackground", "Started in Class GetFriends");
            return Request.get(context, strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.d("Error: ", "Error in GET Request");
                Toast.makeText(context, "There was an Error loading the locations of your friends", Toast.LENGTH_LONG).show();
            } else if (result.equals("401")) {
                //unauthorized -> we need new token -> redirect to Login Activity
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            } else {
                Log.d("JSON response: ", result);

                //we need to handle the conversion from json string to User Object, because user in this json is in format
                //user : { id: ..., firstName: ..., lastName:..}    instead of just user: id
                //therefore we want to ignore user in the deserialization process
                //IMPORTANT! this might need to be changed later to a different approach, if the user data is needed here
                //a different approach could be a second Location class

                //we use the gson builder to add an exclusion strategy, which leads to gson excluding the field user
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("user");      //exclusion happening here!
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });
                Gson gson = gsonBuilder.create();
                //type token is used to load into array list, see: https://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt
                ArrayList<User> friends = gson.fromJson(result, new TypeToken<ArrayList<User>>() {
                }.getType());


                //clear friends list (necessary when coming back)
                myFriends.clear();
                fillMyFriendsListAdapter.clear();

                myFriends.addAll(friends);

                fillMyFriendsListAdapter.setFriends(myFriends);
                fillMyFriendsListAdapter.notifyDataSetChanged();


                //check if locations are empty and set different Layout componentes
                if (myFriends == null || myFriends.size() == 0) {
                    //set visibility of ListView gone and of TextView visible to see the message
                    lvfriends.setVisibility(View.GONE);
                    tv_noFriends.setVisibility(View.VISIBLE);
                }

            }
        }


    }

}
