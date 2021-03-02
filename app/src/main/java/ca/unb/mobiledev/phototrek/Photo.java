package ca.unb.mobiledev.phototrek;

import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Photo {
    private final String mAbsolutePath;
    private LatLng mCoordinates;
    //private final Location mCoordinates;
    private String mDescription;
    private final String mDate;

    public Photo(LatLng coordinates, String description, String date) {
        mAbsolutePath = null;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
    }

    public Photo(String absolutePath, LatLng coordinates, String description, String date) {
        mAbsolutePath = absolutePath;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
    }

    public String getAbsolutePath() { return mAbsolutePath; }

    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) { mDescription = description; }

    public String getDate() {
        return mDate;
    }

    public void setCoordinates(LatLng coordinates){
        mCoordinates = coordinates;
    }
}
