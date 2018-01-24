package com.interactivemedia.backpacker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.Request;
import com.interactivemedia.backpacker.models.Location;
import com.interactivemedia.backpacker.models.User;

import java.util.ArrayList;


/**
 * Adapter fills ListView with ListViewItems
 * Created by Rebecca Durm on 04.01.2018.
 */

public class FillMyFriendsListAdapter extends ArrayAdapter<User> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<User> friends;
    private ArrayList<Location> location;


    public FillMyFriendsListAdapter(Context context, int layoutResourceId, ArrayList<User> friends) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.friends = friends;
    }


    @Override
    public int getCount() {
        if (friends != null) {
            return friends.size();
        }
        return 0;
    }


    @Override
    @NonNull
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }


        TextView textViewName = convertView.findViewById(R.id.tv_friendName);
        ImageView imageViewAvatar = convertView.findViewById(R.id.iv_avatar);


        User friend = friends.get(position);
        String firstName = friend.getFirstName();
        String lastName = friend.getLastName();
        String avatar = friend.getAvatar();
        String friendName = firstName + " " + lastName;


        textViewName.setText(friendName);

        //Set profile picture of friend.
        if (friend.getAvatar() != null) {
            Glide.with(getContext()).load(Request.DOMAIN_URL + avatar).into(imageViewAvatar);
        }

        return convertView;

//        //The Friends Details Activity is only accessed via MyFriendsFragment.
//        //That's why there is no need to do a second request in the FriendDetailsActivity. We can simply hand over the information from this request
//        //read it in the FriendDetailsActivity by getting the extras of the Input.
//        //if the FriendDetailsActivity was accessable from other activities or fragments it will be more useful to
//        //simply hand the userId.
//        view.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), FriendDetailsActivity.class);
//                intent.putExtra("userId", userId);
//                intent.putExtra("firstName", firstName);
//                intent.putExtra("lastName", lastName);
//                intent.putExtra("avatar", avatar);
//                context.startActivity(intent);
//            }
//        });
    }


    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }


    public void setLocation(ArrayList<Location> locations) {
        this.location = locations;
    }


}