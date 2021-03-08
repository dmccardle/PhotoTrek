package ca.unb.mobiledev.phototrek;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataManager extends SQLiteOpenHelper {

    public static final String ALBUMS_TABLE = "ALBUMS";
    public static final String ALBUM_TITLE_COLUMN = "ALBUM_TITLE";


    public DataManager(@Nullable Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAlbumTableStatement = "CREATE TABLE " + ALBUMS_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "" + ALBUM_TITLE_COLUMN + " TEXT" +
                ")";

        // TODO: set up photos tables
        //String createPhotoTableStatement = "";

        sqLiteDatabase.execSQL(createAlbumTableStatement);
        //sqLiteDatabase.execSQL(createPhotoTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // called when a new database version is added
    }

    public boolean addAlbum(Album album) {
        // TODO: call this when adding an album
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ALBUM_TITLE_COLUMN, album.getTitle());
        long result = db.insert(ALBUMS_TABLE, null, contentValues);

        return result != -1;
    }
}
