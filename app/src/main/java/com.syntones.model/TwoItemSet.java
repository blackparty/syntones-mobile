package com.syntones.model;


public class TwoItemSet {

    private Long id;

    private String track_id;

    private int count;

    private float confidence;

    private String recom_song;

    public TwoItemSet() {
        super();
    }

    public TwoItemSet(String track_id, int count) {
        super();
        this.track_id = track_id;
        this.count = count;
    }

    public TwoItemSet(String track_id, String recom_song, int count, float confidence) {
        super();
        this.track_id = track_id;
        this.count = count;
        this.confidence = confidence;
        this.recom_song = recom_song;
    }

    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getRecom_song() {
        return recom_song;
    }

    public void setRecom_song(String recom_song) {
        this.recom_song = recom_song;
    }

}
