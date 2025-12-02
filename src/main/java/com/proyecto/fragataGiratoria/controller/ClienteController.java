package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roles/cliente")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/inicio")
    public String inicioCliente(Model model, Authentication auth) {
        System.out.println("=== DEBUG INICIO CLIENTE ===");
        
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            System.out.println("Usuario autenticado: " + username);
            
            // Buscar usuario en BD
            Usuario usuario = usuarioService.buscarPorNombreUsuario(username);
            
            if (usuario != null) {
                System.out.println("Usuario encontrado en BD: " + usuario.getNombreUsuario());
                model.addAttribute("usuario", usuario);
            } else {
                System.out.println("ERROR: Usuario no encontrado en BD para: " + username);
                
                // SOLUCIÓN DE EMERGENCIA: Crear usuario de prueba
                // Esto es TEMPORAL - debes arreglar tu base de datos
                usuario = new Usuario();
                usuario.setNombreUsuario(username); // Usa el nombre con el que se registró
                usuario.setEmail(username + "@fragata.com");
                
                model.addAttribute("usuario", usuario);
            }
        } else {
            return "redirect:/login";
        }
        
        return "roles/cliente/inicio";
    }

    @GetMapping("/menu")
    public String menuCliente(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            Usuario usuario = usuarioService.buscarPorNombreUsuario(username);
            
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            } else {
                // Misma solución temporal
                usuario = new Usuario();
                usuario.setNombreUsuario(username);
                usuario.setEmail(username + "@fragata.com");
                model.addAttribute("usuario", usuario);
            }
        } else {
            return "redirect:/login";
        }
        
        return "roles/cliente/menu"; 
    }
}