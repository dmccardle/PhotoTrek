package ca.unb.mobiledev.phototrek;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private int mId;
    private String mTitle;
    private int mCoverImagePosition;
    private final List<Photo> mPhotos;

    // Used in creating albums
    public Album(String mTitle) {
        this.mTitle = mTitle;
        this.mCoverImagePosition = -1;
        this.mPhotos = new ArrayList<Photo>();
    }

    // Used in restoring albums from the database
    public Album(int id, String title, int coverImagePosition, List<Photo> photos) {
        this.mId = id;
        this.mTitle = title;
        this.mCoverImagePosition = coverImagePosition;
        this.mPhotos = photos;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getCoverImagePosition() {
        return mCoverImagePosition;
    }

    public void setCoverImagePosition(int position) {
        mCoverImagePosition = position;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

}
