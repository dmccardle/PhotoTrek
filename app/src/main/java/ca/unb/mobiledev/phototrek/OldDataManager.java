package ca.unb.mobiledev.phototrek;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class OldDataManager {
    private static OldDataManager singleInstance = null;

    private List<Album> mAlbums = new ArrayList<>();

    public static OldDataManager getInstance() {
        if(singleInstance == null) {
            singleInstance = new OldDataManager();
            singleInstance.initializeAlbums();
        }
        return singleInstance;
    }

    public List<Album> getAlbums() {
        return mAlbums;
    }

    // Mock data
    private void initializeAlbums() {
        // TODO: load list of albums from JSON data
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
