package ca.unb.mobiledev.phototrek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SavePhotoActivity extends AppCompatActivity {
    public static final String PHOTO_PATH = "ca.unb.mobiledev.phototrek.PHOTO_PATH";
    private Spinner mSpinnerAlbums;
    private EditText mTextDescription;
    private String mPhotoPath;
    private DataManager dataManager;


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
                Photo photo = savePhoto();
                Log.i("PHOTO", photo.getCoordinates().latitude + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~SAVE ACTRIVIY~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + photo.getCoordinates().longitude);
                //dataManager.updatePhoto(photo);
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
        Bitmap thumbnail = BitmapUtils.decodeSampledBitmapFromResource(mPhotoPath, 128, 128);
        mPhotoPreview.setImageBitmap(thumbnail);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Photo savePhoto(){
        LatLng coordinates = new LatLng(1.0,1.0);
        String description = mTextDescription.getText().toString();
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Album album = dataManager.getAllAlbums().get(mSpinnerAlbums.getSelectedItemPosition());
        //coords.latitude
        Photo photo = new Photo(mPhotoPath, coordinates, description, date, album.getId());
        getLocation(photo);
        while(photo.getCoordinates().latitude == 1.0){
            Log.i("PHOTO", photo.getCoordinates().latitude + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LOOP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + photo.getCoordinates().longitude);
            LatLng c = new LatLng(2.0,2.0);
            photo.setCoordinates(c);
        }
        Log.i("PHOTO", photo.getCoordinates().latitude + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LOOP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + photo.getCoordinates().longitude);
        dataManager.addPhoto(photo);

        album.setCoverImagePosition(album.getPhotos().size());
        dataManager.updateAlbum(album);
        finish();
        return photo;
    }

    private void getLocation(Photo photo){
        LocationFinder locationFinder = new LocationFinder(this, this);
        locationFinder.setPhotoLocation(photo, dataManager);
    }

}
