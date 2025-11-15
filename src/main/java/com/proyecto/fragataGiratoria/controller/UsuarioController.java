package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 📋 Listar todos los usuarios
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios"; // Nombre de la vista Thymeleaf
    }

    // ➕ Mostrar formulario de nuevo usuario
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario-form";
    }

    // 💾 Guardar usuario
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    // ✏️ Editar usuario existente
    @GetMapping("/editar/{idUsuario}")
    public String editar(@PathVariable Integer idUsuario, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(idUsuario)
                                         .orElse(new Usuario());
        model.addAttribute("usuario", usuario);
        return "usuario-form";
    }

    // 🗑️ Eliminar usuario
    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(@PathVariable Integer idUsuario) {
        usuarioService.eliminar(idUsuario);
        return "redirect:/usuarios";
    }
}

