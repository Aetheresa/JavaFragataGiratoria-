package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private static final String SESSION_CARRITO = "CARRITO_VISITANTE";

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Obtener carrito para un visitante desde la sesión.
     */
    public List<Producto> obtenerCarritoDeSesion(HttpSession session) {
        List<Producto> carrito = (List<Producto>) session.getAttribute(SESSION_CARRITO);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(SESSION_CARRITO, carrito);
        }
        return carrito;
    }

    /**
     * Agregar producto al carrito. 
     * Si el usuario es null, se agrega en la sesión.
     */
    public void agregarProducto(Usuario usuario, HttpSession session, Producto producto) {
        if (usuario != null) {
            // TODO: Aquí podrías guardar el carrito en base de datos por usuario
            // ejemplo: carritoRepository.agregarProducto(usuario, producto);
        } else {
            List<Producto> carrito = obtenerCarritoDeSesion(session);
            carrito.add(producto);
            session.setAttribute(SESSION_CARRITO, carrito);
        }
    }

    /**
     * Quitar producto del carrito.
     */
    public void quitarProducto(Usuario usuario, HttpSession session, Integer productoId) {
        if (usuario != null) {
            // TODO: Quitar producto del carrito persistente del usuario
        } else {
            List<Producto> carrito = obtenerCarritoDeSesion(session);
            carrito.removeIf(p -> p.getIdProducto().equals(productoId));
            session.setAttribute(SESSION_CARRITO, carrito);
        }
    }

    /**
     * Obtener producto por id (opcional, ayuda a validar)
     */
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }
}
