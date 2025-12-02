package com.proyecto.fragataGiratoria.controller.crud;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crud")
public class CrudMenuController {
    
    @GetMapping("")
    public String mostrarMenuCRUD(Model model) {
        model.addAttribute("titulo", "Sistema CRUD - La Fragata Giratoria");
        model.addAttribute("empresa", "La Fragata Giratoria");
        return "crud/menu";  // Buscará templates/crud/menu.html
    }
}