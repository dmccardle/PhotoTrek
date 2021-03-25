package ca.unb.mobiledev.phototrek;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
    private static File mPhotoFile;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Name Album:");

        final EditText input = new EditText(this);
        input.setText(mAlbum.getTitle());
        alert.setView(input);

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
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

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
                mMap = googleMap;

                List<Photo> photos = mAlbum.getPhotos();
                for (Photo photo : photos) {
                    LatLng marker = photo.getCoordinates();
                    mMap.addMarker(new MarkerOptions().position(marker).title("Marker in Freddy Beach"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
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
            mPhotoFile = BitmapUtils.createImageFile(AlbumPhotoViewActivity.this);

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
            BitmapUtils.createExternalStoragePublicPicture(AlbumPhotoViewActivity.this, mPhotoFile);
            savePhoto();
        }
    }

    private void savePhoto() {
        Intent intent = new Intent(AlbumPhotoViewActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhotoFile.getAbsolutePath());
        startActivity(intent);
    }
}