package ca.unb.mobiledev.phototrek;

import java.util.List;

public class Album {
    private String mTitle;
    private int mCoverImagePosition;
    private final List<Photo> mPhotos;

    public Album(String title, List<Photo> photos) {
        mTitle = title;
        mPhotos = photos;
        mCoverImagePosition = photos.size() - 1;
    }

    public String getTitle() { return mTitle; }

    public void setTitle(String title) { mTitle = title; }

    public int getCoverImagePosition() { return mCoverImagePosition; }

    public void setCoverImagePosition(int position) { mCoverImagePosition = position; }

    public List<Photo> getPhotos() { return mPhotos; }

    @Override
    public String toString() {
        return mTitle;
    }
}
