package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.DetalleCarrito;
import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.PlatilloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CarritoService {

    private static final String SESSION_CARRITO = "CARRITO_VISITANTE";

    @Autowired
    private PlatilloRepository platilloRepository;

    /**
     * Retorna el carrito de sesión como Map<Long, DetalleCarrito>
     */
    public Map<Long, DetalleCarrito> obtenerCarrito(HttpSession session) {

        Map<Long, DetalleCarrito> carrito =
                (Map<Long, DetalleCarrito>) session.getAttribute(SESSION_CARRITO);

        if (carrito == null) {
            carrito = new HashMap<>();
            session.setAttribute(SESSION_CARRITO, carrito);
        }

        return carrito;
    }

    /**
     * Agregar platillo al carrito (visitante o usuario)
     */
    public void agregarPlatillo(Usuario usuario, HttpSession session, Platillo platillo, int cantidad) {

        if (usuario != null) {
            // 🔜 todo: carrito persistente para usuarios registrados
        } else {
            Map<Long, DetalleCarrito> carrito = obtenerCarrito(session);

            if (carrito.containsKey(platillo.getIdPlatillo())) {
                DetalleCarrito detalle = carrito.get(platillo.getIdPlatillo());
                detalle.setCantidad(detalle.getCantidad() + cantidad);
            } else {
                carrito.put(platillo.getIdPlatillo(), new DetalleCarrito(platillo, cantidad));
            }

            session.setAttribute(SESSION_CARRITO, carrito);
        }
    }

    /**
     * Eliminar platillo del carrito
     */
    public void quitarPlatillo(Usuario usuario, HttpSession session, Long platilloId) {

        if (usuario != null) {
            // 🔜 todo: carrito persistente
        } else {
            Map<Long, DetalleCarrito> carrito = obtenerCarrito(session);
            carrito.remove(platilloId);
            session.setAttribute(SESSION_CARRITO, carrito);
        }
    }

    /**
     * Obtener Platillo por ID
     */
    public Optional<Platillo> obtenerPlatilloPorId(Long id) {
        return platilloRepository.findById(id);
    }

    /**
     * Vaciar carrito después del pedido
     */
    public void limpiarCarrito(HttpSession session) {
        session.removeAttribute(SESSION_CARRITO);
    }
}
