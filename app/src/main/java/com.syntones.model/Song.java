package com.syntones.model;

import java.io.File;

/**
 * Created by Courtney Love on 9/12/2016.
 */
public class Song {

    private long songId;

    private String songTitle;

    private String artistName;

    private int distance;

    private File file;

    // connects the song to the artist class

    private Artist artist;

    private String lyrics = "";

    private String filePath;

    public Song() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Song(String songTitle, String artistName) {
        this.artistName = artistName;
        this.songTitle = songTitle;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLyrics() {
        return lyrics;
    }

    public long getSongId() {
        return songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public File getFile() {
        return file;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

}
