package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Playlist;
import com.syntones.model.User;

import java.util.List;


public class LibraryResponse {


    private Message message;
    private List<Playlist> recentlyPlayedPlaylists;

    public LibraryResponse() {
    }


    public LibraryResponse(User user, Message message, List<Playlist> recentlyPlayedPlaylists) {
        this.message = message;
        this.recentlyPlayedPlaylists = recentlyPlayedPlaylists;
    }


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Playlist> getRecentlyPlayedPlaylists() {
        return recentlyPlayedPlaylists;
    }

    public void setRecentlyPlayedPlaylists(List<Playlist> recentPlaylistsPlayed) {
        this.recentlyPlayedPlaylists = recentPlaylistsPlayed;
    }


}
