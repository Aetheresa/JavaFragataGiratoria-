package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crud/metodosdepago") // ✅ URL que quieres
public class MetodoPagoController {

    @GetMapping
    public String listarMetodos(Model model) {
        // Aquí puedes agregar atributos si los necesitas
        return "roles/admin/crud/metododepago/metodosdepago"; 
        // ruta del template dentro de /templates
    }
}
