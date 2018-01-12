package com.interactivemedia.backpacker.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.AddFriendActivity;
import com.interactivemedia.backpacker.activities.FriendsDetailsActivity;
import com.interactivemedia.backpacker.helpers.CustomArrayAdapter;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class FriendsFragment extends Fragment {

    private class GetFriendsTask extends AsyncTask<String, Integer, String> {

        private CustomArrayAdapter adapter;

        public GetFriendsTask(CustomArrayAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("Test", "Started");
            return Request.get(getContext(), strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("JSON response: ", result);
            if(result.equals("error")){
                Log.d("Error: ", "Error in GET Request");
                //Toast.makeText(getContext(), "There was an Error loading the locations of your friends", Toast.LENGTH_LONG).show();
            } else {
                Gson gson = new Gson();
                User[] friends = gson.fromJson(result, User[].class);

                adapter.setUsers(friends);
                adapter.notifyDataSetChanged();
            }

        }
    }

    private User[] friends;
    CustomArrayAdapter arrayAdapter;


    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        //find ListView
        ListView lvfriends = (ListView) view.findViewById(R.id.listViewFriends);

        friends = new User[]{};

        //create adapter containing friends list
        arrayAdapter = new CustomArrayAdapter(getContext(), R.layout.custom_list_item_multiple_choice, friends);
        //assign ArrayAdapter to friends list
        lvfriends.setAdapter(arrayAdapter);

        new GetFriendsTask(arrayAdapter).execute ("/users/5a4cb9154162d41ba096f01d/friends");

        //OnItemClickListener to get friends detail
        lvfriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), FriendsDetailsActivity.class);
                startActivity(intent);
            }
        });

        //set on click listener for add friend button -> open AddFriendActivity
        FloatingActionButton button = view.findViewById(R.id.add_friend_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
