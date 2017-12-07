package com.interactivemedia.backpacker.helpers;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Vali on 05.12.2017.
 * this class handles our colors, currently just used for the markers
 */

public class MarkerColors {
    public static final float[] MARKER_COLORS = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN, BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW};

    /**
     * this function computes the index for our color array to avoid an out of bounds error
     * @param position is the position of the array (e.g. users[position])
     * @return color as hue
     */
    public static float computeColor(int position){
        int index =  position - (int)(Math.floor(position / (double) MARKER_COLORS.length)) * MARKER_COLORS.length;
        return MARKER_COLORS[index];
    }

}
