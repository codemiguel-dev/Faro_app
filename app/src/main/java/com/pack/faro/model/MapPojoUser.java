package com.pack.faro.model;

public class MapPojoUser {

    private String email;

    private String habilidad;
    private double latitud;
    private double longitud;

    public MapPojoUser() {

    }

    public MapPojoUser(String email, String habilidad, double latitud, double longitud) {
        this.email = email;
        this.habilidad = habilidad;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public void setHabilidad(String habilidad) {
        this.habilidad = habilidad;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
