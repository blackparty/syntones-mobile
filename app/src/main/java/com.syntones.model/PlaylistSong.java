package com.syntones.model;


public class PlaylistSong {


    private long songId;
    private User user;
    private long playlistId;

    public PlaylistSong() {

    }

    public PlaylistSong(long songId, long playlistId, User user) {
        this.songId = songId;
        this.playlistId = playlistId;
        this.user = user;
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

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

}
