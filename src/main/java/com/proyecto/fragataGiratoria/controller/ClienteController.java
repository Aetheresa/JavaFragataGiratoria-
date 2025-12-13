package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.PlatilloService;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import com.proyecto.fragataGiratoria.repository.MetodoPagoRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/roles/cliente")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PlatilloService platilloService;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private void cargarDatosUsuario(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Optional<Usuario> usuario = usuarioService.buscarPorNombreUsuario(username);

            if (usuario.isEmpty()) {
                usuario = usuarioService.obtenerPorEmailOptional(username);
            }

            usuario.ifPresent(value -> model.addAttribute("usuario", value));
        }
    }

    @GetMapping("/inicio")
    public String inicioCliente(Model model, Authentication auth) {
        cargarDatosUsuario(model, auth);
        return "roles/cliente/inicio";
    }

    @GetMapping("/menu")
    public String menuCliente(Model model, Authentication auth) {
        cargarDatosUsuario(model, auth);
        model.addAttribute("platillos", platilloService.listarPlatillos());
        return "roles/cliente/menu";
    }
}
