package ca.unb.mobiledev.phototrek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoViewActivity extends AppCompatActivity {
    private String mPhotoPath;
    private String mPhotoDescription;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        extras = getIntent().getExtras();
        mPhotoPath = extras.getString("path");
        mPhotoDescription = extras.getString("Description");

        Bitmap myBitmap = BitmapFactory.decodeFile(mPhotoPath);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageBitmap(myBitmap);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(mPhotoDescription);
    }
}