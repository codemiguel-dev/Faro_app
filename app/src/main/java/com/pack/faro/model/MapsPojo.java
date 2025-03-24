package com.pack.faro.model;

public class MapsPojo {

    private double latitud;
    private double longitud;

    private String detalle;

    public MapsPojo() {

    }


    public MapsPojo(double latitud, double longitud, String detalle) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.detalle = detalle;
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

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
