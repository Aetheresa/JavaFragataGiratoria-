package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/crud/usuarios") // <-- ruta base actualizada
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Listar usuarios (vista admin)
    @GetMapping
    public String listarAdmin(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "roles/admin/crud/usuarios/usuarios"; // Template correcto
    }

    // Formulario nuevo usuario
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "roles/admin/crud/usuarios/usuario-form"; // Debe existir
    }

    // Guardar usuario
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/crud/usuarios";
    }

    // Editar usuario
    @GetMapping("/editar/{idUsuario}")
    public String editar(@PathVariable Integer idUsuario, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(idUsuario)
                                         .orElse(new Usuario());
        model.addAttribute("usuario", usuario);
        return "roles/admin/crud/usuarios/usuario-form";
    }

    // Eliminar usuario
    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(@PathVariable Integer idUsuario) {
        usuarioService.eliminar(idUsuario);
        return "redirect:/crud/usuarios";
    }
}
