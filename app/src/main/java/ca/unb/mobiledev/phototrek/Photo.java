package ca.unb.mobiledev.phototrek;

import com.google.android.gms.maps.model.LatLng;

public class Photo {
    private int mId;
    private final String mAbsolutePath;
    private LatLng mCoordinates;
    //private final Location mCoordinates;
    private String mDescription;
    private final String mDate;
    private Integer mAlbumId;

    public Photo(LatLng coordinates, String description, String date) {
        mAbsolutePath = null;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
        this.mAlbumId = null;
    }

    public Photo(String absolutePath, LatLng coordinates, String description, String date, int albumId) {
        mAbsolutePath = absolutePath;
        mCoordinates = coordinates;
        mDescription = description;
        mDate = date;
        mAlbumId = albumId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    public LatLng getCoordinates() {
        return mCoordinates;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setCoordinates(LatLng coordinates) {
        mCoordinates = coordinates;
    }

    public Integer getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(Integer albumId) {
        this.mAlbumId = albumId;
    }
}
