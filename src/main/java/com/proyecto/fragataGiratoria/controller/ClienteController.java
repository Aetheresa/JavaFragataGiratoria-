package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/roles/cliente")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Cargar los datos del usuario logueado usando Authentication.
     */
    private void cargarDatosUsuario(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName(); // puede ser username o email, depende de tu configuración
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorNombreUsuario(username);

            if (usuarioOpt.isPresent()) {
                model.addAttribute("usuario", usuarioOpt.get());
            } else {
                // Si no lo encuentra por "nombreUsuario", intentamos por email (por si getName() devuelve email)
                Optional<Usuario> usuarioPorEmail = usuarioService.obtenerPorEmailOptional(username);
                if (usuarioPorEmail.isPresent()) {
                    model.addAttribute("usuario", usuarioPorEmail.get());
                } else {
                    // advertencia — no se encontró registro en la DB para el principal autenticado
                    System.out.println("ADVERTENCIA: Usuario autenticado '" + username + "' no encontrado en la base de datos.");
                    // opcional: model.addAttribute("usuario", null);  Thymeleaf debe manejar el null con operador ternario si es necesario
                }
            }
        }
    }

    /**
     * Página de inicio para clientes: /roles/cliente/inicio
     */
    @GetMapping("/inicio")
    public String inicioCliente(Model model, Authentication auth) {
        cargarDatosUsuario(model, auth);
        return "roles/cliente/inicio";
    }

    /**
     * Página de menú para clientes: /roles/cliente/menu
     */
    @GetMapping("/menu")
    public String menuCliente(Model model, Authentication auth) {
        cargarDatosUsuario(model, auth);
        return "roles/cliente/menu";
    }
}

