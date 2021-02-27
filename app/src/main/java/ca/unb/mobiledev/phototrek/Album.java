package ca.unb.mobiledev.phototrek;

import java.util.List;

public class Album {
    private final String mTitle;
    private final int mCoverImagePosition;
    private final List<Photo> mPhotos;

    public Album(String title, List<Photo> photos) {
        mTitle = title;
        mPhotos = photos;
        mCoverImagePosition = photos.size() - 1;
    }

    public String getTitle() { return mTitle; }

    public int getCoverImagePosition() { return mCoverImagePosition; }

    public List<Photo> getPhotos() { return mPhotos; }

    @Override
    public String toString() {
        return mTitle;
    }
}
