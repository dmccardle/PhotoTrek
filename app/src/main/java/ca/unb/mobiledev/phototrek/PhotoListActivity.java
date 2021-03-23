package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoListActivity extends AppCompatActivity{
//public class PhotoListActivity extends AppCompatActivity{
    public static final String ALBUM_POSITION = "ca.unb.mobiledev.phototrek.ALBUM_POSITION";
    private GridLayoutManager mPhotoLayoutManager;
    private PhotoListRecyclerAdapter mPhotoListRecyclerAdapter;
    private int mAlbumPosition;
    private Album mAlbum;
    private GoogleMap mMap;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    private static String mCurrentPhotoPath;
    private Context mContext;
    private static File mPhotoFile;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
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

        Intent intent = getIntent();
        mAlbumPosition = intent.getIntExtra(ALBUM_POSITION, -1);
        mAlbum = dataManager.getAllAlbums().get(mAlbumPosition);

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
                Marker mMarker;
                //BitmapDescriptorFactory myBitmap;

                List<Photo> photos = mAlbum.getPhotos();
                for(Photo photo : photos) {
                    LatLng markerCoor = photo.getCoordinates();
                    //myBitmap = BitmapDescriptorFactory.fromPath(photo.getAbsolutePath());
                    mMarker = mMap.addMarker(new MarkerOptions().position(markerCoor).title("Marker in Freddy Beach"));
                    mMarker.setTag(photo);
                    //mMarker.setIcon(BitmapDescriptorFactory.fromPath(photo.getAbsolutePath()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerCoor));
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Photo photo = (Photo) marker.getTag();
                            Intent intent = new Intent(mContext, PhotoViewActivity.class);
                            intent.putExtra("path", photo.getAbsolutePath());
                            intent.putExtra("Description", photo.getDescription());
                            mContext.startActivity(intent);
                            return true;
                        }
                    });
                }
            }
        });
    }

//    @Override
//    public boolean onMarkerClick(final Marker marker){
//        // need to send image somehow
//
//        Photo photo = (Photo) marker.getTag();
//        Intent intent = new Intent(this, PhotoViewActivity.class);
//        intent.putExtra("path", photo.getAbsolutePath());
//        intent.putExtra("Description", photo.getDescription());
//        this.startActivity(intent);
//        return true;
//    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go. The file will be internal to the application.
            mPhotoFile = null;
            mPhotoFile = BitmapUtils.createImageFile(PhotoListActivity.this);

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
            // When the photo is taken successfully, create a copy in external storage, and launch the save photo form activity
            BitmapUtils.createExternalStoragePublicPicture(PhotoListActivity.this, mPhotoFile);
            savePhoto();
        }
    }

    private void savePhoto() {
        Intent intent = new Intent(PhotoListActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhotoFile.getAbsolutePath());
        startActivity(intent);
    }
}