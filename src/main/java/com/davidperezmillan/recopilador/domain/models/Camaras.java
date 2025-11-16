package com.davidperezmillan.recopilador.domain.models;

public enum Camaras {
    SALON("http://192.168.68.127", "salon", "salon"),
    HABITACION("http://192.168.68.128", "hab", "habitacion"),

    CAM2("http://192.168.68.129", "cam2", "camara2"),;

    private final String url;
    private final String nombre;
    private final String directorio;

    Camaras(String url, String nombre, String directorio) {
        this.url = url;
        this.nombre = nombre;
        this.directorio = directorio;
    }

    public String getUrl() {
        return url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDirectorio() {
        return directorio;
    }


}
