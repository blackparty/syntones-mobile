package com.syntones.model;

import java.util.List;

/**
 * Created by Courtney Love on 9/12/2016.
 */
public class Playlist {


    private long playlistId;

    private String playlistName;

    private List<Song> songs;

    private User user;

    private long songId;

    public Playlist() {

    }

    public Playlist(long playlistId,long songId){
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }


}
