package ca.unb.mobiledev.phototrek;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataManager extends SQLiteOpenHelper {

    private final String TAG = "DataManager";

    public DataManager(@Nullable Context context) {
        super(context, "database.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlbumSchema.SQL_CREATE_ALBUM_TABLE);
        db.execSQL(PhotoSchema.SQL_CREATE_PHOTO_TABLE);
        createFirstAlbum(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(AlbumSchema.SQL_DELETE_ALBUM_TABLE);
        db.execSQL(PhotoSchema.SQL_DELETE_PHOTO_TABLE);
        db.execSQL(AlbumSchema.SQL_CREATE_ALBUM_TABLE);
        db.execSQL(PhotoSchema.SQL_CREATE_PHOTO_TABLE);
        createFirstAlbum(db);
    }


    private void createFirstAlbum(SQLiteDatabase db) {
        Album firstAlbum = new Album("First Album");
        ContentValues cv = new ContentValues();
        cv.put(AlbumSchema.ALBUM_TITLE_COLUMN, firstAlbum.getTitle());
        cv.put(AlbumSchema.ALBUM_COVER_IMAGE_POSITION, firstAlbum.getCoverImagePosition());
        db.insert(AlbumSchema.ALBUM_TABLE, null, cv);
    }

    public boolean addAlbum(Album album) {
        Log.i(TAG, "Starting to add album...");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(AlbumSchema.ALBUM_TITLE_COLUMN, album.getTitle());
        cv.put(AlbumSchema.ALBUM_COVER_IMAGE_POSITION, album.getCoverImagePosition());
        long result = db.insert(AlbumSchema.ALBUM_TABLE, null, cv);

        Log.i(TAG, "Finished adding album.");
        return result != -1;
    }

    public Album getAlbumById(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = AlbumSchema._ID + " = " + albumId;

        Cursor cursor = db.query(
                AlbumSchema.ALBUM_TABLE,
                null,
                selection,
                null,
                null,
                null,
                null
        );

        List<Album> albums = getAlbumsFromCursor(cursor);
        return albums.get(0);
    }

    public List<Album> getAllAlbums() {
        Log.i(TAG, "Starting to get all albums...");
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                AlbumSchema.ALBUM_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<Album> albums = getAlbumsFromCursor(cursor);
        Log.i(TAG, "Finished getting all albums.");
        return albums;
    }

    private List<Album> getAlbumsFromCursor(Cursor cursor) {
        List<Album> albums = new ArrayList<>();
        while (cursor.moveToNext()) {
            int albumId = cursor.getInt(0);
            String albumTitle = cursor.getString(1);
            int albumCoverImagePosition = cursor.getInt(2);
            List<Photo> albumPhotos = getPhotosForAlbum(albumId);
            albums.add(new Album(albumId, albumTitle, albumCoverImagePosition, albumPhotos));
        }
        return albums;
    }

    public boolean deleteAlbum(Album album) {
        if (!deletePhotosFromAlbum(album)) {
            Log.e(TAG, "Could not delete photos from album.");
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = AlbumSchema._ID + " = " + album.getId();

        int count = db.delete(AlbumSchema.ALBUM_TABLE, selection, null);
        return count == 1;
    }

    private boolean deletePhotosFromAlbum(Album album) {
        List<Photo> photoList = getPhotosForAlbum(album.getId());

        SQLiteDatabase db = this.getWritableDatabase();
        String selection = PhotoSchema.PHOTO_ALBUM_COLUMN + " = " + album.getId();

        int count = db.delete(PhotoSchema.PHOTO_TABLE, selection, null);

        return count == photoList.size();
    }

    public boolean updateAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AlbumSchema.ALBUM_TITLE_COLUMN, album.getTitle());
        cv.put(AlbumSchema.ALBUM_COVER_IMAGE_POSITION, album.getCoverImagePosition());
        String selection = AlbumSchema._ID + " = " + album.getId();

        int count = db.update(AlbumSchema.ALBUM_TABLE, cv, selection, null);
        return count == 1;
    }

    public boolean addPhoto(Photo photo) {
        Log.i(TAG, "Starting to add photo...");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PhotoSchema.PHOTO_PATH_COLUMN, photo.getAbsolutePath());
        cv.put(PhotoSchema.PHOTO_LAT_COLUMN, photo.getCoordinates().latitude);
        cv.put(PhotoSchema.PHOTO_LNG_COLUMN, photo.getCoordinates().longitude);
        cv.put(PhotoSchema.PHOTO_DESCRIPTION_COLUMN, photo.getDescription());
        cv.put(PhotoSchema.PHOTO_DATE_COLUMN, photo.getDate());
        cv.put(PhotoSchema.PHOTO_ALBUM_COLUMN, photo.getAlbumId());

        long result = db.insert(PhotoSchema.PHOTO_TABLE, null, cv);
        Log.i(TAG, "Finished adding photo.");
        return result != -1;
    }

    private List<Photo> getPhotosForAlbum(int albumId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PhotoSchema.PHOTO_ALBUM_COLUMN + " = " + albumId;
        Cursor cursor = db.query(
                PhotoSchema.PHOTO_TABLE,
                null,
                selection,
                null,
                null,
                null,
                null
        );

        return getPhotosFromCursor(cursor);
    }

    public List<Photo> getAllPhotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                PhotoSchema.PHOTO_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return getPhotosFromCursor(cursor);
    }

    private List<Photo> getPhotosFromCursor(Cursor cursor) {
        List<Photo> photos = new ArrayList<>();
        while (cursor.moveToNext()) {
            int photoId = cursor.getInt(0);
            String photoPath = cursor.getString(1);
            double photoLat = cursor.getDouble(2);
            double photoLng = cursor.getDouble(3);
            LatLng photoLatLng = new LatLng(photoLat, photoLng);
            String photoDescription = cursor.getString(4);
            String photoDate = cursor.getString(5);
            int photoAlbum = cursor.getInt(6);

            photos.add(new Photo(photoId, photoPath, photoLatLng, photoDescription, photoDate, photoAlbum));
        }
        return photos;
    }

    public boolean deletePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = PhotoSchema._ID + " = " + photo.getId();

        int count = db.delete(PhotoSchema.PHOTO_TABLE, selection, null);
        return count == 1;
    }

    public boolean updatePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PhotoSchema.PHOTO_PATH_COLUMN, photo.getAbsolutePath());
        cv.put(PhotoSchema.PHOTO_LAT_COLUMN, photo.getCoordinates().latitude);
        cv.put(PhotoSchema.PHOTO_LNG_COLUMN, photo.getCoordinates().longitude);
        cv.put(PhotoSchema.PHOTO_DESCRIPTION_COLUMN, photo.getDescription());
        cv.put(PhotoSchema.PHOTO_DATE_COLUMN, photo.getDate());
        cv.put(PhotoSchema.PHOTO_ALBUM_COLUMN, photo.getAlbumId());

        String selection = PhotoSchema._ID + " = " + photo.getId();

        int count = db.update(PhotoSchema.PHOTO_TABLE, cv, selection, null);
        return count == 1;
    }

    public static class AlbumSchema implements BaseColumns {
        public static final String ALBUM_TABLE = "ALBUMS";
        public static final String ALBUM_TITLE_COLUMN = "ALBUM_TITLE";
        public static final String ALBUM_COVER_IMAGE_POSITION = "ALBUM_COVER";

        public static final String SQL_CREATE_ALBUM_TABLE =
                "CREATE TABLE " + AlbumSchema.ALBUM_TABLE + " (" +
                        AlbumSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        AlbumSchema.ALBUM_TITLE_COLUMN + " TEXT, " +
                        AlbumSchema.ALBUM_COVER_IMAGE_POSITION + " INTEGER )";

        public static final String SQL_DELETE_ALBUM_TABLE =
                "DROP TABLE IF EXISTS " + AlbumSchema.ALBUM_TABLE;
    }

    public static class PhotoSchema implements BaseColumns {
        public static final String PHOTO_TABLE = "PHOTOS";
        public static final String PHOTO_PATH_COLUMN = "PHOTO_PATH";
        public static final String PHOTO_LAT_COLUMN = "PHOTO_LAT";
        public static final String PHOTO_LNG_COLUMN = "PHOTO_LNG";
        public static final String PHOTO_DESCRIPTION_COLUMN = "PHOTO_DESCRIPTION";
        public static final String PHOTO_DATE_COLUMN = "PHOTO_DATE";
        public static final String PHOTO_ALBUM_COLUMN = "PHOTO_ALBUM";

        public static final String SQL_CREATE_PHOTO_TABLE =
                "CREATE TABLE " + PhotoSchema.PHOTO_TABLE + " (" +
                        PhotoSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        PhotoSchema.PHOTO_PATH_COLUMN + " TEXT," +
                        PhotoSchema.PHOTO_LAT_COLUMN + " REAL," +
                        PhotoSchema.PHOTO_LNG_COLUMN + " REAL," +
                        PhotoSchema.PHOTO_DESCRIPTION_COLUMN + " TEXT," +
                        PhotoSchema.PHOTO_DATE_COLUMN + " TEXT," +
                        PhotoSchema.PHOTO_ALBUM_COLUMN + " INTEGER," +
                        " FOREIGN KEY (" + PhotoSchema.PHOTO_ALBUM_COLUMN + ") REFERENCES " + AlbumSchema.ALBUM_TABLE + "(" + AlbumSchema._ID + "))";

        public static final String SQL_DELETE_PHOTO_TABLE =
                "DROP TABLE IF EXISTS " + PhotoSchema.PHOTO_TABLE;
    }

}
