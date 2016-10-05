package com.syntones.remote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.syntones.model.SavedOfflineSongs;

import java.util.ArrayList;

/**
 * Created by Courtney Love on 10/2/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE = "syntones_db";
    private static final String TABLE = "saved_songs_tbl";
    private static final String SONG_ID = "song_id";
    private static final String ARTIST_NAME = "artist_name";
    private static final String SONG_TITLE = "song_title";
    private static final String FILE_PATH = "file_path";
    private static final String LYRICS = "lyrics";
    private static final String USER_ID = "user_id";

    public DBHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists " + TABLE + " (id integer primary key," + USER_ID + " text," + SONG_ID + " long," + ARTIST_NAME + " text," + SONG_TITLE + " text," + FILE_PATH + " text," + LYRICS + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertSavedSong(SavedOfflineSongs savedOfflineSongs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(USER_ID, savedOfflineSongs.getUserName());
            contentValues.put(SONG_ID, savedOfflineSongs.getSongId());
            contentValues.put(ARTIST_NAME, savedOfflineSongs.getArtistName());
            contentValues.put(SONG_TITLE, savedOfflineSongs.getSongTitle());
            contentValues.put(FILE_PATH, savedOfflineSongs.getFilePath());
            contentValues.put(LYRICS, savedOfflineSongs.getLyrics());

            db.insert(TABLE, null, contentValues);

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public ArrayList<SavedOfflineSongs> getAllSavedOfflineSongsFromUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SavedOfflineSongs> savedOfflineSongsArrayList = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE + " where " + USER_ID + " = '" + username + "'", null);

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                SavedOfflineSongs savedOfflineSongs = new SavedOfflineSongs();
                savedOfflineSongs.setUserName(cursor.getString(cursor.getColumnIndex(USER_ID)));
                savedOfflineSongs.setSongId(cursor.getString(cursor.getColumnIndex(SONG_ID)));
                savedOfflineSongs.setArtistName(cursor.getString(cursor.getColumnIndex(ARTIST_NAME)));
                savedOfflineSongs.setSongTitle(cursor.getString(cursor.getColumnIndex(SONG_TITLE)));
                savedOfflineSongs.setFilePath(cursor.getString(cursor.getColumnIndex(FILE_PATH)));
                savedOfflineSongs.setLyrics(cursor.getString(cursor.getColumnIndex(LYRICS)));
                savedOfflineSongsArrayList.add(savedOfflineSongs);
                cursor.moveToNext();
            }

            return savedOfflineSongsArrayList;
        } catch (Exception e) {
            return null;
        }

    }
}
