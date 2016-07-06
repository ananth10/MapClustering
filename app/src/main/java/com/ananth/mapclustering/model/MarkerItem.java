package com.ananth.mapclustering.model;

import com.ananth.mapclustering.interfaces.ClusterItem;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Babu on 7/6/2016.
 */
public class MarkerItem implements ClusterItem, com.google.maps.android.clustering.ClusterItem {
    private final LatLng mPosition;

    public MarkerItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

}
