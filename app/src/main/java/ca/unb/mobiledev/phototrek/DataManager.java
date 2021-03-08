package ca.unb.mobiledev.phototrek;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataManager extends SQLiteOpenHelper {

    public DataManager(@Nullable Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlbumSchema.SQL_CREATE_ALBUM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // called when a new database version is added
    }

    public boolean addAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(AlbumSchema.ALBUM_TITLE_COLUMN, album.getTitle());
        long result = db.insert(AlbumSchema.ALBUM_TABLE, null, contentValues);

        return result != -1;
    }

    public List<Album> getAllAlbums() {
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

        return getAlbumsFromCursor(cursor);
    }

    private List<Album> getAlbumsFromCursor(Cursor cursor) {
        List<Album> albums = new ArrayList<>();
        while (cursor.moveToNext()) {
            int albumId = cursor.getInt(0);
            String albumTitle = cursor.getString(1);
            albums.add(new Album(albumId, albumTitle));
        }
        return albums;
    }

    public boolean deleteAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = AlbumSchema._ID + " = " + album.getId();

        int count = db.delete(AlbumSchema.ALBUM_TABLE, selection, null);
        return count == 1;
    }

    public boolean updateAlbum(Album album) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AlbumSchema.ALBUM_TITLE_COLUMN, album.getTitle());
        String selection = AlbumSchema._ID + " = " + album.getId();

        int count = db.update(AlbumSchema.ALBUM_TABLE, contentValues, selection, null);
        return count == 1;
    }

    public static class AlbumSchema implements BaseColumns {
        public static final String ALBUM_TABLE = "ALBUMS";
        public static final String ALBUM_TITLE_COLUMN = "ALBUM_TITLE";

        public static final String SQL_CREATE_ALBUM_TABLE =
                "CREATE TABLE " + AlbumSchema.ALBUM_TABLE + " (" +
                        AlbumSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        AlbumSchema.ALBUM_TITLE_COLUMN + " TEXT)";

        public static final String SQL_DELETE_ALBUM_TABLE =
                "DROP TABLE IF EXISTS " + AlbumSchema.ALBUM_TABLE;
    }

}
