package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Song;

import java.util.List;

public class ListenResponse {
    private List<Song> recommendedSongs;
    private Message message;


    public ListenResponse() {


    }

    public List<Song> getRecommendedSongs() {
        return recommendedSongs;
    }


    public void setRecommendedSongs(List<Song> recommendedSongs) {
        this.recommendedSongs = recommendedSongs;
    }


    public Message getMessage() {
        return message;
    }


    public void setMessage(Message message) {
        this.message = message;
    }


}
