package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.service.UsuarioService;
import com.proyecto.fragataGiratoria.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    // FORMULARIO DE REGISTRO
    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // PROCESAR REGISTRO
    @PostMapping
    public String registrarUsuario(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // Validación de campos JPA/Bean Validation
        if (result.hasErrors()) {
            model.addAttribute("mensajeError", "Por favor corrija los errores del formulario.");
            return "registro";
        }

        // Validar contraseñas
        if (!usuario.getPasswordHash().equals(confirmPassword)) {
            model.addAttribute("mensajeError", "Las contraseñas no coinciden.");
            return "registro";
        }

        try {
            // Buscar rol CLIENTE correctamente
            Rol cliente = rolService.obtenerPorNombre("CLIENTE")
                    .orElseThrow(() -> new IllegalArgumentException("No existe el rol CLIENTE."));

            // Asignar rol antes del registro
            usuario.setRol(cliente);

            // Registrar usuario (password se encripta en el servicio)
            usuarioService.registrarNuevoUsuario(usuario);

            return "redirect:/login?success";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "registro";

        } catch (Exception ex) {
            model.addAttribute("mensajeError", "Ocurrió un error al registrar el usuario.");
            return "registro";
        }
    }
}
