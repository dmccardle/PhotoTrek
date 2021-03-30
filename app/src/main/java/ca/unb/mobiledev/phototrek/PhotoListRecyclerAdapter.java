package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoListRecyclerAdapter extends RecyclerView.Adapter<PhotoListRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Photo> mPhotos;
    private final LayoutInflater mLayoutInflater;

    public PhotoListRecyclerAdapter(Context context, List<Photo> photos) {
        mContext = context;
        mPhotos = photos;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = mPhotos.get(position);
        String photoPath = photo.getAbsolutePath();
        String thumbnailPath = photo.getThumbnailPath();

        if (photoPath == null) {
            holder.mPhoto.setImageResource(R.drawable.ic_photo_placeholder);
        } else {
            ImageViewCompat.setImageTintList(holder.mPhoto, null);
            Bitmap thumbnail = BitmapFactory.decodeFile(thumbnailPath); // 128
            holder.mPhoto.setImageBitmap(thumbnail);
        }

        holder.mCurrentPosition = position;
        holder.mPhotos = mPhotos;
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mPhoto;
        public int mCurrentPosition;
        public List<Photo> mPhotos;

        public ViewHolder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.image_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Photo photo = mPhotos.get(mCurrentPosition);
                    Intent intent = new Intent(mContext, ViewPhotoActivity.class);
                    intent.putExtra(ViewPhotoActivity.PHOTO_POSITION, mCurrentPosition);
                    intent.putExtra(ViewPhotoActivity.ALBUMID, photo.getAlbumId());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
