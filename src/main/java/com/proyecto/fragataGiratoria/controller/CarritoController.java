package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.CarritoService;
import com.proyecto.fragataGiratoria.service.ProductoServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoServiceImpl productoService;

    /**
     * Ver carrito: público, no requiere login
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        // Obtenemos el carrito de sesión (para visitantes)
        List<Producto> productos = carritoService.obtenerCarritoDeSesion(session);
        model.addAttribute("productos", productos);
        return "carrito"; // Vista Thymeleaf carrito.html
    }

    /**
     * Agregar producto al carrito: requiere login
     */
    @PostMapping("/agregar/{productoId}")
    public String agregarAlCarrito(@AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer productoId,
            HttpSession session) {

        if (usuario == null) {
            // Redirigir al login si no está logueado
            return "redirect:/login";
        }

        Producto producto = productoService.obtenerProductoPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        carritoService.agregarProducto(usuario, session, producto);

        return "redirect:/carrito";
    }

    /**
     * Quitar producto del carrito: requiere login
     */
    @PostMapping("/quitar/{productoId}")
    public String quitarDelCarrito(@AuthenticationPrincipal Usuario usuario,
            @PathVariable Integer productoId,
            HttpSession session) {

        if (usuario == null) {
            return "redirect:/login";
        }

        carritoService.quitarProducto(usuario, session, productoId);
        return "redirect:/carrito";
    }
}
