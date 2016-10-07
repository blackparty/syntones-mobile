package com.syntones.model;


import java.sql.Timestamp;
import java.util.Date;

public class TemporaryDB {

    private Long id;

    private String user_id;

    private long song_id;

    private Timestamp date;

    public TemporaryDB() {
        super();
    }

    public TemporaryDB(Long id, String user_id, long song_id, Timestamp date) {
        this.id = id;
        this.user_id = user_id;
        this.song_id = song_id;
        this.date = date;
    }

    public TemporaryDB(Long id, String user_id, long song_id) {
        super();
        this.id = id;
        this.user_id = user_id;
        this.song_id = song_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
