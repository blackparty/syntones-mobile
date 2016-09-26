package com.syntones.model;


import java.util.List;


public class Tag {

    private long id;

    private String tag;

    private List<TagSynonym> synonyms;

    public Tag() {


    }


    public List<TagSynonym> getSynonyms() {
        return synonyms;
    }


    public void setSynonyms(List<TagSynonym> synonyms) {
        this.synonyms = synonyms;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Tag [id=" + id + ", tag=" + tag + "]";
    }


}
