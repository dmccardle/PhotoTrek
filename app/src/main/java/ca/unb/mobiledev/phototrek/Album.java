package ca.unb.mobiledev.phototrek;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private int mId;
    private String mTitle;
    private int mCoverImagePosition;
    private final List<Photo> mPhotos;

    public Album(String mTitle) {
        this.mTitle = mTitle;
        this.mCoverImagePosition = -1;
        this.mPhotos = new ArrayList<Photo>();
    }

    public Album(int id, String mTitle, List<Photo> photos) {
        this.mId = id;
        this.mTitle = mTitle;
        this.mCoverImagePosition = -1;
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
