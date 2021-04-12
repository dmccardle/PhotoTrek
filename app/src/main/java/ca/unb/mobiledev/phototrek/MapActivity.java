package ca.unb.mobiledev.phototrek;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 10;
    static final int SAVE_PHOTO = 11;
    private static final String APP_TAG = "PhotoTrek";
    private GoogleMap mMap;
    private static String mCurrentPhotoPath;
    private Context mContext;
    private static File mPhotoFile;
    private DataManager dataManager;
    final private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;

        dataManager = new DataManager(this);

        FloatingActionButton fab = findViewById(R.id.fab_new_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        FloatingActionButton refreshFab = findViewById(R.id.fab_refresh_map);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });

        initializeMap();

        int PERMISSION_ALL = 1;
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(!LocationFinder.getInstance(this).isRequestingLocationRequest())
            LocationFinder.getInstance(this).startLocationUpdates();
    }

    private void stopLocationUpdates() {
        LocationFinder.getInstance(this).stopLocationUpdates();
    }

    // Uses the res/menu/menu_maps.xml resource to populate the actions.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    // Handles clicks on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_album) {
            openAlbum();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAlbum() {
        Intent intent = new Intent(MapActivity.this, AlbumListActivity.class);
        startActivity(intent);
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (LocationFinder.getInstance(MapActivity.this).isLocationEnabled()) {
                    mMap = googleMap;
                    List<Album> albums = dataManager.getAllAlbums();
                    for (Album album : albums) {
                        List<Photo> photos = album.getPhotos();
                        for (int i = 0; i < photos.size(); i++) {
                            Photo photo = photos.get(i);
                            LatLng marker = photo.getCoordinates();
                            Marker mMarker;
                            mMarker = mMap.addMarker(new MarkerOptions().position(marker).title("Marker in Freddy Beach"));
                            mMarker.setTag(photo);
                            mMarker.setPosition(photo.getCoordinates());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    Photo photo = (Photo) marker.getTag();
                                    List<Photo> photos = album.getPhotos();
                                    int position = photos.indexOf(photo);
                                    Intent intent = new Intent(mContext, ViewPhotoActivity.class);
                                    intent.putExtra(ViewPhotoActivity.PHOTO_POSITION, position);
                                    intent.putExtra(ViewPhotoActivity.ALBUMID, photo.getAlbumId());
                                    mContext.startActivity(intent);
                                    return true;
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(MapActivity.this, "PhotoTrek requires access to your location. Please turn the service on.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        if (LocationFinder.getInstance(this).isLocationEnabled()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go. The file will be internal to the application.
                mPhotoFile = null;
                mPhotoFile = BitmapUtils.createImageFile(MapActivity.this);

                if (mPhotoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "ca.unb.mobiledev.phototrek.provider",
                            mPhotoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            Toast.makeText(this, "PhotoTrek requires access to your location. Please turn the service on.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // When the photo is taken successfully, create a copy in shared external storage, create a thumbnail in external storage, and launch the save photo form activity
            Bitmap thumbnailSource = BitmapUtils.decodeSampledBitmapFromResource(mPhotoFile.getAbsolutePath(), 256, 256);
            File thumbnail = BitmapUtils.createThumbnailInStorage(MapActivity.this, thumbnailSource);
            File photo = BitmapUtils.createExternalStoragePublicPicture(MapActivity.this, mPhotoFile);

            // Delete original file stored in the external storage, keep the photo in the shared external storage
            mPhotoFile.delete();
            mPhotoFile = photo;
            savePhoto(thumbnail);
        }

        if (requestCode == SAVE_PHOTO) {
            initializeMap();
        }
    }

    private void savePhoto(File thumbnail) {
        Intent intent = new Intent(MapActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhotoFile.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.THUMBNAIL_PATH, thumbnail.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.ALBUM,0);
        intent.putExtra(SavePhotoActivity.TYPE, "ADD");
        startActivityForResult(intent, SAVE_PHOTO);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}