package com.syntones.model;

/**
 * Created by Courtney Love on 10/2/2016.
 */

public class SavedOfflineSongs {
    private String artistName, songTitle, filePath, lyrics, songId, userName;

    public SavedOfflineSongs() {
    }

    public SavedOfflineSongs(String artistName, String songTitle, String filePath, String lyrics, String songId, String userName) {
        this.artistName = artistName;
        this.songTitle = songTitle;
        this.filePath = filePath;
        this.lyrics = lyrics;
        this.songId = songId;
        this.userName = userName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
