package com.syntones.model;

import java.util.ArrayList;

public class SearchResultModel {

    private ArrayList<SearchModel> songs;
    private ArrayList<SearchModel> artists;
    boolean artistNan;
    boolean songNan;

    public SearchResultModel() {

    }

    public ArrayList<SearchModel> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<SearchModel> songs) {
        this.songs = songs;
    }

    public ArrayList<SearchModel> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<SearchModel> artists) {
        this.artists = artists;
    }

    public boolean isArtistNan() {
        return artistNan;
    }

    public void setArtistNan(boolean artistNan) {
        this.artistNan = artistNan;
    }

    public boolean isSongNan() {
        return songNan;
    }

    public void setSongNan(boolean songNan) {
        this.songNan = songNan;
    }


}
