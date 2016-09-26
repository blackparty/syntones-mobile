package com.syntones.model;


public class TagSynonym {


    private long id;


    private long tagId;


    private String synonym;

    public TagSynonym() {
    }

    public TagSynonym(long tagId, String synonym) {
        this.tagId = tagId;
        this.synonym = synonym;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }


}
