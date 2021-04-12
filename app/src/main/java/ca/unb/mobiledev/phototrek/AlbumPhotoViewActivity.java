package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AlbumPhotoViewActivity extends AppCompatActivity {
	
    public static final String ALBUM_POSITION = "ca.unb.mobiledev.phototrek.ALBUM_POSITION";
    private GridLayoutManager mPhotoLayoutManager;
    private PhotoListRecyclerAdapter mPhotoListRecyclerAdapter;
    private int mAlbumPosition;
    private Album mAlbum;
    private GoogleMap mMap;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    static final int SAVE_PHOTO = 11;
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

        FloatingActionButton refreshFab = findViewById(R.id.fab_refresh_map);
        refreshFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
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
    protected void onRestart() {
        mAlbum = dataManager.getAllAlbums().get(mAlbumPosition);
        displayPhotos();
        if (mMap != null) mMap.clear();
        initializeMap();
        super.onRestart();
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
        LocationFinder.getInstance(this).startLocationUpdates();
    }

    private void stopLocationUpdates() {
        LocationFinder.getInstance(this).stopLocationUpdates();
    }


    // Uses the res/menu/menu_albums.xml resource to populate the actions.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_view, menu);
        return true;
    }

    // Handles clicks on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_album) {
            showEditAlbumAlert();
        } else if (id == R.id.action_delete_album) {
            showDeleteAlbumAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditAlbumAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlbumCreationDialog));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_album, null);
        EditText input = dialogView.findViewById(R.id.txtAlbumName);
        input.setText(mAlbum.getTitle());
        alert.setView(dialogView);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mAlbum.setTitle(input.getText().toString());
                dataManager.updateAlbum(mAlbum);
                refreshActivity();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void showDeleteAlbumAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlbumCreationDialog));

        alert.setTitle("Are you sure you want to delete album '" + mAlbum.getTitle() + "'?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dataManager.deleteAlbum(mAlbum);
                finish();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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
                if (LocationFinder.getInstance(AlbumPhotoViewActivity.this).isLocationEnabled()) {
                    mMap = googleMap;
                    List<Photo> photos = mAlbum.getPhotos();
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
                                List<Photo> photos = mAlbum.getPhotos();
                                int position = photos.indexOf(photo);
                                Intent intent = new Intent(mContext, ViewPhotoActivity.class);
                                intent.putExtra(ViewPhotoActivity.PHOTO_POSITION, position);
                                intent.putExtra(ViewPhotoActivity.ALBUMID, photo.getAlbumId());
                                mContext.startActivity(intent);
                                return true;
                            }
                        });
                    }
                } else {
                    Toast.makeText(AlbumPhotoViewActivity.this, "PhotoTrek requires access to your location. Please turn the service on.", Toast.LENGTH_LONG).show();
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
                mPhotoFile = BitmapUtils.createImageFile(AlbumPhotoViewActivity.this);

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
            // When the photo is taken successfully, create a copy in external storage, and launch the save photo form activity
            Bitmap thumbnailSource = BitmapUtils.decodeSampledBitmapFromResource(mPhotoFile.getAbsolutePath(), 256, 256);
            File thumbnail = BitmapUtils.createThumbnailInStorage(AlbumPhotoViewActivity.this, thumbnailSource);
            File photo = BitmapUtils.createExternalStoragePublicPicture(AlbumPhotoViewActivity.this, mPhotoFile);

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
        Intent intent = new Intent(AlbumPhotoViewActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhotoFile.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.THUMBNAIL_PATH, thumbnail.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.ALBUM, mAlbum.getId());
        intent.putExtra(SavePhotoActivity.TYPE, "ADD");
        startActivityForResult(intent, SAVE_PHOTO);
    }
}