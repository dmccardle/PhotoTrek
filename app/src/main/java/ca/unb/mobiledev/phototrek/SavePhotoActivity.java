package ca.unb.mobiledev.phototrek;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SavePhotoActivity extends AppCompatActivity {
    public static final String PHOTO_PATH = "ca.unb.mobiledev.phototrek.PHOTO_PATH";
    public static final String THUMBNAIL_PATH = "ca.unb.mobiledev.phototrek.THUMBNAIL_PATH";
    public static final String PHOTO_POSITION = "ca.unb.mobiledev.phototrek.PHOTO_POSITION";
    public static final String TYPE = "ca.unb.mobiledev.phototrek.TYPE";
    public static final String ALBUM = "ca.unb.mobiledev.phototrek.ALBUM";
    public static final String DESCRIPTION = "ca.unb.mobiledev.phototrek.DESCRIPTION";
    private Spinner mSpinnerAlbums;
    private EditText mTextDescription;
    private String mPhotoPath;
    private String mThumbnailPath;
    private DataManager dataManager;
    private Bitmap mThumbnail;
    private int mPhotoPosition;
    private String mType;
    private Album originalAlbum;
    private Photo mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationFinder locationFinder = LocationFinder.getInstance(this);

        setContentView(R.layout.activity_photo_save);
        setupUI(findViewById(R.id.parent_view_container));

        dataManager = new DataManager(this);

        mSpinnerAlbums = (Spinner) findViewById(R.id.spinner_albums);
        List<Album> albums = dataManager.getAllAlbums();
        ArrayAdapter<Album> adapterAlbums =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albums);
        adapterAlbums.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAlbums.setAdapter(adapterAlbums);

        mTextDescription = (EditText) findViewById(R.id.textbox_description);
        ImageView mPhotoPreview = (ImageView) findViewById(R.id.photo_preview);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SavePhotoActivity.this, FullscreenPhotoActivity.class);
                intent.putExtra(FullscreenPhotoActivity.PHOTO_PATH, mPhotoPath);
                startActivity(intent);
            }
        });

        Button mSaveButton = (Button) findViewById(R.id.btn_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.equals("ADD")) { savePhoto(); }
                else if (mType.equals("EDIT")) { editPhoto(); }
            }
        });
        Button mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(PHOTO_PATH);

        // Preselect album in spinner
        int albumId = intent.getIntExtra(ALBUM, 0);
        if (albumId != 0) {
            originalAlbum = dataManager.getAlbumById(albumId);
            for(int i = 0; i < albums.size(); i++) {
                if (albums.get(i).getId() == albumId)
                    mSpinnerAlbums.setSelection(i);
            }
        }
        mThumbnailPath = intent.getStringExtra(THUMBNAIL_PATH);

        // Load image in background
        SquareBitmapLoader squareBitmapLoader = new SquareBitmapLoader(mPhotoPreview);
        squareBitmapLoader.execute(mPhotoPath);

        mType = intent.getStringExtra(TYPE);
        if (mType.equals("ADD")) {
            getSupportActionBar().setTitle(R.string.title_activity_photoView_new);
        } else if (mType.equals("EDIT")) {
            String description = intent.getStringExtra(DESCRIPTION);
            mPhotoPosition = intent.getIntExtra(PHOTO_POSITION, 0);
            mPhoto = originalAlbum.getPhotos().get(mPhotoPosition);
            mTextDescription.setText(description);
            getSupportActionBar().setTitle(R.string.title_activity_photoView_edit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mType.equals("EDIT"))
            getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    // Handles clicks on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_photo) {
            showDeletePhotoAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void showDeletePhotoAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlbumCreationDialog));
        alert.setTitle("Are you sure you want to delete the photo?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dataManager.deletePhoto(mPhoto);
                int originalAlbumSize = dataManager.getAlbumById(originalAlbum.getId()).getPhotos().size();
                originalAlbum.setCoverImagePosition(originalAlbumSize - 1);
                dataManager.updateAlbum(originalAlbum);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void savePhoto() {
        if (mSpinnerAlbums.getSelectedItem() == null) {
            Toast toast = Toast.makeText(this, "Album is required", Toast.LENGTH_LONG);
            toast.show();
        } else {
            LocationFinder locationFinder = LocationFinder.getInstance(this);
            LatLng coordinates = locationFinder.getLocation();
            String description = mTextDescription.getText().toString();
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            Album album = dataManager.getAllAlbums().get(mSpinnerAlbums.getSelectedItemPosition());
            Photo photo = new Photo(mPhotoPath, coordinates, description, date, album.getId(), mThumbnailPath);
            dataManager.addPhoto(photo);

            album.setCoverImagePosition(album.getPhotos().size());
            dataManager.updateAlbum(album);
            finish();
        }
    }

    private void editPhoto() {
        if (mSpinnerAlbums.getSelectedItem() == null) {
            Toast toast = Toast.makeText(this, "Album is required", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Album destinationAlbum = dataManager.getAllAlbums().get(mSpinnerAlbums.getSelectedItemPosition());

            int previousAlbumId = mPhoto.getAlbumId();

            mPhoto.setAlbumId(destinationAlbum.getId());
            mPhoto.setDescription(mTextDescription.getText().toString());
            dataManager.updatePhoto(mPhoto);

            if (previousAlbumId != mPhoto.getAlbumId()) {
                updatePreviousAlbumCoverImagePosition(previousAlbumId);
                updateDestinationAlbumCoverImagePosition(mPhoto.getAlbumId());
            }

            finish();
        }
    }

    private void updatePreviousAlbumCoverImagePosition(int previousAlbumId) {
        Album previousAlbum = dataManager.getAlbumById(previousAlbumId);
        previousAlbum.setCoverImagePosition(previousAlbum.getCoverImagePosition() - 1);
        dataManager.updateAlbum(previousAlbum);
    }

    private void updateDestinationAlbumCoverImagePosition(int destinationAlbumId) {
        Album destinationAlbum = dataManager.getAlbumById(destinationAlbumId);
        destinationAlbum.setCoverImagePosition(destinationAlbum.getCoverImagePosition() + 1);
        dataManager.updateAlbum(destinationAlbum);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextDescription.getWindowToken(), 0);
    }
}
