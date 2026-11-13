package com.gimnasio.notificaciones.negocio;

import com.gimnasio.notificaciones.dato.NotificacionDao;
import com.gimnasio.notificaciones.modelo.Notificacion;
import java.util.List;

public class NotificacionN {

    private NotificacionDao dao;

    public NotificacionN() {
        this.dao = new NotificacionDao();
    }

    public boolean registrarNotificacion(Notificacion n) {
        if (n.getTipo() == null || n.getTipo().trim().isEmpty()
                || n.getMensaje() == null || n.getMensaje().trim().isEmpty()) {
            System.out.println("Validación fallida: tipo y mensaje son obligatorios.");
            return false;
        }
        boolean sinUsuario = n.getUsuarioId() == null || n.getUsuarioId() <= 0;
        boolean sinRol = n.getRolDestino() == null || n.getRolDestino().trim().isEmpty();
        if (sinUsuario && sinRol) {
            System.out.println("Validación fallida: se requiere usuarioId o rolDestino.");
            return false;
        }
        if (sinUsuario) {
            n.setUsuarioId(null);
        }
        if (sinRol) {
            n.setRolDestino(null);
        }
        return dao.registrarNotificacion(n);
    }

    public List<Notificacion> listarPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            return null;
        }
        return dao.listarPorUsuario(usuarioId);
    }

    public List<Notificacion> listarPorRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            return null;
        }
        return dao.listarPorRol(rol.trim());
    }

    public boolean marcarLeida(int id) {
        if (id <= 0) {
            return false;
        }
        return dao.marcarLeida(id);
    }
}
