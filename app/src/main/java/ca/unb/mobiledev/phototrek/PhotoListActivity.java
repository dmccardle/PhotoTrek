package ca.unb.mobiledev.phototrek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoListActivity extends AppCompatActivity {
    public static final String ALBUM_POSITION = "ca.unb.mobiledev.phototrek.ALBUM_POSITION";
    private GridLayoutManager mPhotoLayoutManager;
    private PhotoListRecyclerAdapter mPhotoListRecyclerAdapter;
    private int mAlbumPosition;
    private Album mAlbum;
    private GoogleMap mMap;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    private static String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_new_photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        Intent intent = getIntent();
        mAlbumPosition = intent.getIntExtra(ALBUM_POSITION, -1);
        mAlbum = DataManager.getInstance().getAlbums().get(mAlbumPosition);

        initializeMap();
        displayPhotos();
        setToolbarLabel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoListRecyclerAdapter.notifyDataSetChanged();
        mPhotoListRecyclerAdapter.notifyDataSetChanged();
    }

    private void setToolbarLabel() {
        getSupportActionBar().setTitle(mAlbum.getTitle());
    }

    private void displayPhotos() {
        RecyclerView mRecyclerAlbums = (RecyclerView) findViewById(R.id.photo_list);
        mPhotoLayoutManager = new GridLayoutManager(this, 5);

        List<Photo> photos = mAlbum.getPhotos();
        mPhotoListRecyclerAdapter = new PhotoListRecyclerAdapter(this, photos);

        mRecyclerAlbums.setLayoutManager(mPhotoLayoutManager);
        mRecyclerAlbums.setAdapter(mPhotoListRecyclerAdapter);
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                List<Photo> photos = mAlbum.getPhotos();
                for(Photo photo : photos) {
                    LatLng sydney = new LatLng(-34, 151);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(PhotoListActivity.this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.unb.mobiledev.phototrek.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            BitmapUtils.broadcastMediaScan(PhotoListActivity.this, mCurrentPhotoPath);
            savePhoto();
        }
    }

    private void savePhoto() {
        Intent intent = new Intent(PhotoListActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mCurrentPhotoPath);
        startActivity(intent);
    }
}