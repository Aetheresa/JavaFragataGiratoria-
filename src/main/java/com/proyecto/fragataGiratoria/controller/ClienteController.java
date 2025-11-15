package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.config.CustomUserDetails;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Página de inicio para clientes
     * URL: /cliente/inicio
     */
    @GetMapping("/cliente/inicio")
    public String inicioCliente(Model model,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Obtener usuario autenticado desde Spring Security
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado(userDetails);

        // Si no hay usuario logueado, redirigir a login
        if (usuario == null) {
            return "redirect:/login";
        }

        // Enviar datos del usuario a la vista Thymeleaf
        model.addAttribute("usuario", usuario);

        // Retornar la plantilla Thymeleaf cliente/inicio.html
        return "cliente/inicio";
    }
}
