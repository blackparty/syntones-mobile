package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Song;

import java.util.List;


public class GeneratePlaylistResponse {
    private List<Song> songs;
    private Message message;

    public GeneratePlaylistResponse() {
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
