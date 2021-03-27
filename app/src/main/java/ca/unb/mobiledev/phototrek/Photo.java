package ca.unb.mobiledev.phototrek;

import com.google.android.gms.maps.model.LatLng;

public class Photo {
    private int mId;
    private final String mAbsolutePath;
    private LatLng mCoordinates;
    private String mDescription;
    private final String mDate;
    private Integer mAlbumId;
    private final String mThumbnailPath;

    // Used in creating photos
    public Photo(String absolutePath, LatLng coordinates, String description, String date, int albumId, String thumbnailPath) {
        this.mAbsolutePath = absolutePath;
        this.mCoordinates = coordinates;
        this.mDescription = description;
        this.mDate = date;
        this.mAlbumId = albumId;
        this.mThumbnailPath = thumbnailPath;
    }

    // Used in restoring photos from the database
    public Photo(int id, String absolutePath, LatLng coordinates, String description, String date, int albumId, String thumbnailPath) {
        this.mId = id;
        this.mAbsolutePath = absolutePath;
        this.mCoordinates = coordinates;
        this.mDescription = description;
        this.mDate = date;
        this.mAlbumId = albumId;
        this.mThumbnailPath = thumbnailPath;
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

    public String getThumbnailPath() { return mThumbnailPath; }
}
