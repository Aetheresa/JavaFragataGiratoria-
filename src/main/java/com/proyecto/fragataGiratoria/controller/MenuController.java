package com.proyecto.fragataGiratoria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String mostrarMenu(Model model) {
        // Aquí puedes agregar atributos si tu template los necesita, por ejemplo:
        // model.addAttribute("productos", listaDeProductos);

        return "menu"; // Thymeleaf template: menu.html
    }
}
