package com.gimnasio.rutina.modelo;

public class Ejercicio {
    private int id;
    private int Id_Categoria; 
    private String nombre;
    private String descripcion;
    private String videoURL;

    public Ejercicio() {}

    public Ejercicio(int id, int Id_Categoria, String nombre, String descripcion, String videoURL) {
        this.id = id;
        this.Id_Categoria = Id_Categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.videoURL = videoURL;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_Categoria() { return Id_Categoria; }
    public void setId_Categoria(int Id_Categoria) { this.Id_Categoria = Id_Categoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getVideoURL() { return videoURL; }
    public void setVideoURL(String videoURL) { this.videoURL = videoURL; }
}