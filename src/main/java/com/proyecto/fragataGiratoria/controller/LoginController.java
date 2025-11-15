package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success,
            Model model) {

        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales incorrectas o campos vacíos.");
        }

        if (logout != null) {
            model.addAttribute("mensajeExito", "Has cerrado sesión correctamente.");
        }

        if (success != null) {
            model.addAttribute("mensajeExito", "Registro exitoso. Ahora puedes iniciar sesión.");
        }

        return "login"; // Thymeleaf login.html
    }
}
