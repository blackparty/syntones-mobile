package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Song;

import java.util.List;


public class SongListResponse {
    private Message message;
    private List<Song> songList;

    public SongListResponse() {
    }

    public SongListResponse(Message message, List<Song> songList) {
        this.message = message;
        this.songList = songList;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<Song> getSongs() {
        return this.songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

}
