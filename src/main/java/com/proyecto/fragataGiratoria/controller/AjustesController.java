package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjustesController {

    @GetMapping("/admin/ajustes")  // <-- URL exacta que quieres usar
    public String mostrarAjustes() {
        // Devuelve el template Thymeleaf: templates/roles/admin/ajustes.html
        return "roles/admin/ajustes";
    }
}
