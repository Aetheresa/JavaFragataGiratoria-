package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crud")
public class CrudRedirectController {
    
    @GetMapping("/productos")
    public String redirectToProductos() {
        // Redirige a la ruta REAL de tu ProductoController
        return "redirect:/productos";
    }
}