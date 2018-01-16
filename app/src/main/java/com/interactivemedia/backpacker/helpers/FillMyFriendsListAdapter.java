package com.interactivemedia.backpacker.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.activities.FriendsDetailsActivity;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * Adapter fills ListView with ListViewItems
 * Created by Rebecca Durm on 04.01.2018.
 */

public class FillMyFriendsListAdapter extends ArrayAdapter<Location>{
    private Context context;
    private int layoutResourceId;
    private ArrayList<User> friends;
    private ArrayList<Location> location;

    ImageView imageView;


    public FillMyFriendsListAdapter(Context context, int layoutResourceId, ArrayList<User> friends){
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.friends = friends;
    }


    @Override
    public int getCount(){
        if(friends != null) {
            return friends.size();
        }
        return 0;
    }


    @SuppressLint("CutPasteId")
    @Override @NonNull
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        FriendsHolder holder;



        if (view==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, viewGroup, false);

            holder = new FriendsHolder();

            holder.friendName = view.findViewById(R.id.tv_friendName);
            holder.profilePicture = view.findViewById(R.id.iv_avatar);
            view.setTag(holder);


        } else {
            holder = (FriendsHolder) view.getTag();
        }

        final User friend = friends.get(position);
        final String firstName = friend.getFirstName();
        final String lastName = friend.getLastName();
        final String userId=friend.getId();
        String friendName = firstName + " " + lastName;


        holder.friendName.setText(friendName);


        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsDetailsActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                context.startActivity(intent);
            }
        });



        return view;
    }

    static class FriendsHolder
    {
        TextView friendName;
        ImageView profilePicture;
    }


    public void setFriends(ArrayList<User> friends){
        this.friends = friends;
    }


    public void setLocation(ArrayList<Location> locations){
        this.location = locations;
    }
}