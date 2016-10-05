package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Playlist;
import com.syntones.model.User;

import java.util.List;

/**
 * Created by Courtney Love on 9/15/2016.
 */
public class LoginResponse {

    private Message message;
    private List<Playlist> recentlyPlayedPlaylists;
    private User user;
    public LoginResponse() {
    }


    public LoginResponse(User user, Message message, List<Playlist> recentlyPlayedPlaylists) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
