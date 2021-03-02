package ca.unb.mobiledev.phototrek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavePhotoActivity extends AppCompatActivity {
    public static final String PHOTO_PATH = "ca.unb.mobiledev.phototrek.PHOTO_PATH";
    private Spinner mSpinnerAlbums;
    private EditText mTextDescription;
    private ImageView mPhotoPreview;
    private Button mSaveButton;
    private Button mCancelButton;
    private String mPhotoPath;
    private LocationFinder locationFinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_photo);

        mSpinnerAlbums = (Spinner) findViewById(R.id.spinner_albums);
        List<Album> albums = DataManager.getInstance().getAlbums();
        ArrayAdapter<Album> adapterAlbums =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, albums);
        adapterAlbums.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAlbums.setAdapter(adapterAlbums);

        mTextDescription = (EditText) findViewById(R.id.textbox_description);
        mPhotoPreview = (ImageView) findViewById(R.id.photo_preview);

        mSaveButton = (Button) findViewById(R.id.btn_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = savePhoto();
                getLocation(photo);
            }
        });
        mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(PHOTO_PATH);
        Bitmap thumbnail = BitmapUtils.decodeSampledBitmapFromResource(mPhotoPath, 128, 128);
        mPhotoPreview.setImageBitmap(thumbnail);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Photo savePhoto(){
        String coordinates = "";
        String description = mTextDescription.getText().toString();
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Photo photo = new Photo(mPhotoPath, coordinates, description, date);

        Album album = DataManager.getInstance().getAlbums().get(mSpinnerAlbums.getSelectedItemPosition());
        album.getPhotos().add(photo);
        finish();
        return photo;
    }

    private void getLocation(Photo photo){
        locationFinder = new LocationFinder(this, this);
        locationFinder.setPhotoLocation(photo);
    }

}
