package com.ananth.mapclustering.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Babu on 7/6/2016.
 */
public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    LatLng getPosition();
}
