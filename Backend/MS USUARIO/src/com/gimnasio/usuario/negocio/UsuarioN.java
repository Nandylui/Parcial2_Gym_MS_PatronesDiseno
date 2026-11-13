package com.gimnasio.usuario.negocio;

import com.gimnasio.usuario.dato.UsuarioDao;
import com.gimnasio.usuario.modelo.Usuario;
import java.util.List;

public class UsuarioN {

    // Atributo especificado en el diagrama
    private UsuarioDao dao;

    public UsuarioN() {
        this.dao = new UsuarioDao();
    }

    public List<Usuario> listarUsuario() {
        return dao.listarUsuarios();
    }

    public Usuario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }
    
    
    public Usuario buscarPorEmail(String email) {
        return dao.buscarPorEmail(email);
    }

    public boolean registrarUsuario(Usuario usuario) {
        // Regla de Negocio: Verificar que el email no exista antes de registrar
        if (dao.buscarPorEmail(usuario.getEmail()) != null) {
            System.out.println("Error: El correo " + usuario.getEmail() + " ya está registrado.");
            return false;
        }
        return dao.registrarUsuario(usuario);
    }

    public boolean actualizarUsuario(Usuario usuario) {
        // Podrías agregar más validaciones aquí en el futuro
        return dao.actualizarUsuario(usuario);
    }

    public boolean eliminarUsuario(int id) {
        return dao.eliminarUsuario(id);
    }

    public List<String> obtenerRoles(int id) {
        return dao.obtenerRolesPorUsuario(id);
    }
}