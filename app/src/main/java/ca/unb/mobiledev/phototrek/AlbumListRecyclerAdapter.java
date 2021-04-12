package ca.unb.mobiledev.phototrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumListRecyclerAdapter extends RecyclerView.Adapter<AlbumListRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Album> mAlbums;
    private final LayoutInflater mLayoutInflater;

    public AlbumListRecyclerAdapter(Context context, List<Album> album) {
        mContext = context;
        mAlbums = album;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Album album = mAlbums.get(position);
        holder.mTextTitle.setText(album.getTitle());

        try {
            setCoverPhoto(holder, album);
        } catch (IndexOutOfBoundsException e) {
            holder.mCoverPhoto.setImageResource(R.drawable.ic_empty_album);
        }

        holder.mCurrentPosition = position;
    }

    private void setCoverPhoto(ViewHolder holder, Album album) throws IndexOutOfBoundsException {
        String photoPath = album.getPhotos().get(album.getCoverImagePosition()).getAbsolutePath();
        String thumbnailPath = album.getPhotos().get(album.getCoverImagePosition()).getThumbnailPath();
        if (photoPath == null) {
            holder.mCoverPhoto.setImageResource(R.drawable.ic_empty_album);
        } else {
            ImageViewCompat.setImageTintList(holder.mCoverPhoto, null);
            Bitmap thumbnail = BitmapFactory.decodeFile(thumbnailPath);
            holder.mCoverPhoto.setImageBitmap(thumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextTitle;
        public final ImageView mCoverPhoto;
        public int mCurrentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_album_title);
            mCoverPhoto = (ImageView) itemView.findViewById(R.id.image_cover_photo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AlbumPhotoViewActivity.class);
                    intent.putExtra(AlbumPhotoViewActivity.ALBUM_POSITION, mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}







