package com.interactivemedia.backpacker.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.interactivemedia.backpacker.R;
import com.interactivemedia.backpacker.helpers.MarkerColors;
import com.interactivemedia.backpacker.models.User;

import java.util.ArrayList;


/**
 * Created by Vali on 05.12.2017.
 * this array adapter is used for the list view in the filter sidebar in map fragment
 * it is needed to have icons with different colors in our list
 */

public class CustomArrayAdapter extends ArrayAdapter<User>{

    private Context context;
    private int layoutResourceId;
    private ArrayList<User> users;

    public CustomArrayAdapter(Context context, int layoutResourceId, ArrayList<User> users){
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.users = users;
    }

    @Override @NonNull
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        View row = convertView;
        UserHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UserHolder();
            holder.colorIcon = row.findViewById(R.id.color_icon);
            holder.checkedTextView = row.findViewById(R.id.name);

            row.setTag(holder);
        }
        else
        {
            holder = (UserHolder) row.getTag();
        }

        User user = users.get(position);
        String name = user.getFirstName() + " " + user.getLastName();
        holder.checkedTextView.setText(name);

        float hueColor = MarkerColors.computeColor(position);
        int color = ColorUtils.HSLToColor(new float[]{hueColor, 1, 0.5f});
        //change the color of the icon in the list, this might be changed later, when we are not depending on the MARKER_COLORS anymore
        holder.colorIcon.setColorFilter(color , PorterDuff.Mode.SRC_IN);
        return row;
    }

    @Override
    public int getCount(){
        if(users != null){
            return users.size();
        }
        return 0;
    }

    public void setUsers(ArrayList<User> users){
        this.users = users;
    }


    static class UserHolder
    {
        ImageView colorIcon;
        CheckedTextView checkedTextView;
    }
}


