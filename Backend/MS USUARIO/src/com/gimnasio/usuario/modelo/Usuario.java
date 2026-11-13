package com.gimnasio.usuario.modelo;

import java.sql.Date;
import com.google.gson.annotations.SerializedName;

public class Usuario {
    
    private int id;
    private int ci;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena; 
    private Integer telefono;
    @SerializedName("fecha_nac")
    private Date fechaNac; 
    private String genero;
    private String estado;

    public Usuario() {
    }

    public Usuario(int id, int ci, String nombre, String apellido, String email, String contrasena, Integer telefono, Date fechaNac, String genero, String estado) {
        this.id = id;
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena; 
        this.telefono = telefono;
        this.fechaNac = fechaNac;
        this.genero = genero;
        this.estado = estado;
    }

    // --- GETTERS Y SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCi() { return ci; }
    public void setCi(int ci) { this.ci = ci; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; } 
    public void setContrasena(String contrasena) { this.contrasena = contrasena; } 

    public Integer getTelefono() { return telefono; }
    public void setTelefono(Integer telefono) { this.telefono = telefono; }

    public Date getFechaNac() { return fechaNac; }
    public void setFechaNac(Date fechaNac) { this.fechaNac = fechaNac; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}