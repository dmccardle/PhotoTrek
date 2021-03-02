package ca.unb.mobiledev.phototrek;

import com.google.android.gms.maps.model.LatLng;

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

    public Album createAlbum(String title) {
        List<Photo> photos = new ArrayList<>();
        LatLng latlong = new LatLng(10,10);
        photos.add(new Photo(latlong, "description", "10/10/21"));
        photos.add(new Photo(latlong, "description2", "11/10/21"));
        photos.add(new Photo(latlong, "description3", "12/10/21"));
        return new Album(title, photos);
    }

    public void addAlbum(Album album){
        mAlbums.add(album);
    }
}
