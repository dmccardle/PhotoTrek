package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final String APP_TAG = "PhotoTrek";
    private GoogleMap mMap;
    private static File mPhotoFile;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataManager = new DataManager(this);

        FloatingActionButton fab = findViewById(R.id.fab_new_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        initializeMap();

        BitmapUtils.isStoragePermissionGranted(this);
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
                mMap = googleMap;

                List<Album> albums = dataManager.getAllAlbums();
                for (Album album : albums) {
                    List<Photo> photos = album.getPhotos();
                    for (Photo photo : photos) {
                        // Add a marker in Fredericton and move the camera
                        LatLng marker = photo.getCoordinates();
                        mMap.addMarker(new MarkerOptions().position(marker).title("Marker in Freddy Beach"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    }
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
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
    }

    private void savePhoto(File thumbnail) {
        Intent intent = new Intent(MapActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhotoFile.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.THUMBNAIL_PATH, thumbnail.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.ALBUM,0);
        intent.putExtra(SavePhotoActivity.TYPE, "ADD");
        startActivity(intent);
    }
}