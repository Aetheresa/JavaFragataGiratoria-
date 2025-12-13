package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AyudaController {

    @GetMapping("/roles/admin/ayuda")  // <-- URL exacta
    public String mostrarAyuda() {
        // Devuelve el template Thymeleaf: templates/roles/admin/ayuda.html
        return "roles/admin/ayuda";
    }
}
