package com.pack.faro.model;

public class ModelUser {

    String uid, email, name, habilidad, image;

    long timestamp;

    public ModelUser() {
    }

    public ModelUser(String uid, String email, String name, String habilidad, String image, long timestamp) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.habilidad = habilidad;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
