package com.syntones.response;

import com.syntones.model.Tag;

import java.util.List;


public class SynonymResponse {
    private List<String> synonyms;
    private Tag tag;

    public SynonymResponse() {
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "SynonymResponse [synonyms=" + synonyms + ", tag=" + tag + "]";
    }


}
