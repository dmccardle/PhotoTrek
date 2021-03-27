package ca.unb.mobiledev.phototrek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SavePhotoActivity extends AppCompatActivity {
    public static final String PHOTO_PATH = "ca.unb.mobiledev.phototrek.PHOTO_PATH";
    public static final String THUMBNAIL_PATH = "ca.unb.mobiledev.phototrek.THUMBNAIL_PATH";
    private Spinner mSpinnerAlbums;
    private EditText mTextDescription;
    private String mPhotoPath;
    private String mThumbnailPath;
    private DataManager dataManager;
    private Bitmap mThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        dataManager = new DataManager(this);

        mSpinnerAlbums = (Spinner) findViewById(R.id.spinner_albums);
        List<Album> albums = dataManager.getAllAlbums();
        ArrayAdapter<Album> adapterAlbums =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albums);
        adapterAlbums.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAlbums.setAdapter(adapterAlbums);

        mTextDescription = (EditText) findViewById(R.id.textbox_description);
        ImageView mPhotoPreview = (ImageView) findViewById(R.id.photo_preview);

        Button mSaveButton = (Button) findViewById(R.id.btn_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });
        Button mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(PHOTO_PATH);
        mThumbnailPath = intent.getStringExtra(THUMBNAIL_PATH);

        mThumbnail = BitmapFactory.decodeFile(mThumbnailPath);
        mPhotoPreview.setImageBitmap(mThumbnail);
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
            LatLng coordinates = new LatLng(0.0, 0.0);
            String description = mTextDescription.getText().toString();
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Album album = dataManager.getAllAlbums().get(mSpinnerAlbums.getSelectedItemPosition());
            Photo photo = new Photo(mPhotoPath, coordinates, description, date, album.getId(), mThumbnailPath);
            getLocation(photo);
            dataManager.addPhoto(photo);

            album.setCoverImagePosition(album.getPhotos().size());
            dataManager.updateAlbum(album);
            finish();
        }
    }

    private void getLocation(Photo photo) {
        LocationFinder locationFinder = new LocationFinder(this, this);
        locationFinder.setPhotoLocation(photo);
    }

}
