package com.syntones.model;

public class SearchModel {
    private long id;
    private int[] vector;
    private float cos_angle;
    private float degrees;

    public SearchModel() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

    public float getCos_angle() {
        return cos_angle;
    }

    public void setCos_angle(float cos_angle) {
        this.cos_angle = cos_angle;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

}
