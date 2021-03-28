package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewPhotoActivity extends AppCompatActivity {
    public static final String PHOTO_POSITION = "ca.unb.mobiledev.phototrek.PHOTO_POSITION";
    public static final String ALBUMID = "ca.unb.mobiledev.phototrek.ALBUMID";

    private TextView mDateText;
    private TextView mLocationText;
    private TextView mDescriptionText;
    private ImageView mPhotoPreview;

    private DataManager dataManager;
    private int mPhotoPosition;
    private int mAlbumId;
    private Photo mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();
        mPhotoPosition = intent.getIntExtra(PHOTO_POSITION, 0);
        mAlbumId = intent.getIntExtra(ALBUMID, 0);

        dataManager = new DataManager(this);
        mPhoto = dataManager.getAlbumById(mAlbumId).getPhotos().get(mPhotoPosition);

        mDateText = (TextView) findViewById(R.id.dateTextView);
        mLocationText = (TextView) findViewById(R.id.locationTextViiew);
        mDescriptionText = (TextView) findViewById(R.id.descriptionTextView);
        mPhotoPreview = (ImageView) findViewById(R.id.photo_preview);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPhotoActivity.this, FullscreenPhotoActivity.class);
                intent.putExtra(FullscreenPhotoActivity.PHOTO_PATH, mPhoto.getAbsolutePath());
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        String dateString;
        try {
            Date date = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(mPhoto.getDate());
            dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            dateString = mPhoto.getDate();
        }
        mDateText.setText(dateString);
        mLocationText.setText(mPhoto.getCoordinates().toString());
        mDescriptionText.setText(mPhoto.getDescription());
        mPhotoPreview.setImageBitmap(BitmapFactory.decodeFile(mPhoto.getThumbnailPath()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_album) {
            editPhoto();
        } else if (id == R.id.action_delete_album) {
            showDeletePhotoAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    private void editPhoto() {
        Intent intent = new Intent(ViewPhotoActivity.this, SavePhotoActivity.class);
        intent.putExtra(SavePhotoActivity.PHOTO_PATH, mPhoto.getAbsolutePath());
        intent.putExtra(SavePhotoActivity.THUMBNAIL_PATH, mPhoto.getThumbnailPath());
        intent.putExtra(SavePhotoActivity.ALBUM, mAlbumId);
        intent.putExtra(SavePhotoActivity.DESCRIPTION, mPhoto.getDescription());
        intent.putExtra(SavePhotoActivity.PHOTO_POSITION, mPhotoPosition);
        intent.putExtra(SavePhotoActivity.TYPE, "EDIT");
        startActivity(intent);
    }

    private void showDeletePhotoAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlbumCreationDialog));
        alert.setTitle("Are you sure you want to delete the photo?");

        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dataManager.deletePhoto(mPhoto);
                Album album = dataManager.getAlbumById(mAlbumId);
                int photoListSize = album.getPhotos().size();
                album.setCoverImagePosition(photoListSize - 1);
                dataManager.updateAlbum(album);
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
}
