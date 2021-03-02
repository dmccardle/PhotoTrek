package ca.unb.mobiledev.phototrek;

import android.location.Location;
import android.net.Uri;

public class Photo {
    private final String mAbsolutePath;
    private String mCoordinates;
    //private final Location mCoordinates;
    private final String mDescription;
    private final String mDate;

    public Photo(String coordinates, String description, String date) {
        mAbsolutePath = null;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
    }

    public Photo(String absolutePath, String coordinates, String description, String date) {
        mAbsolutePath = absolutePath;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
    }

    public String getAbsolutePath() { return mAbsolutePath; }

    public String getCoordinates() {
        return mCoordinates;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDate() {
        return mDate;
    }

    public void setCoordinates(String coordinates){
        mCoordinates = coordinates;
    }
}
