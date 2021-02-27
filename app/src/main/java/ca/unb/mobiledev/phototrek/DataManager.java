package ca.unb.mobiledev.phototrek;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager singleInstance = null;

    private List<Album> mAlbums = new ArrayList<>();

    public static DataManager getInstance() {
        if(singleInstance == null) {
            singleInstance = new DataManager();
            singleInstance.initializeAlbums();
        }
        return singleInstance;
    }

    public List<Album> getAlbums() {
        return mAlbums;
    }

    // Mock data
    private void initializeAlbums() {
        mAlbums.add(createAlbum("Waterfall"));
        mAlbums.add(createAlbum("PoI's"));
    }

    private Album createAlbum(String title) {
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo("lat, long", "description", "10/10/21"));
        photos.add(new Photo("lat, long", "description2", "11/10/21"));
        photos.add(new Photo("lat, long", "description3", "12/10/21"));
        return new Album(title, photos);
    }
}
