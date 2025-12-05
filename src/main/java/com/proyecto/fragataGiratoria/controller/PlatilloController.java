package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.repository.PlatilloRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crud/platillos")
public class PlatilloController {

    private final PlatilloRepository platilloRepository;

    public PlatilloController(PlatilloRepository platilloRepository) {
        this.platilloRepository = platilloRepository;
    }

    @GetMapping
    public String listarPlatillos(Model model) {

        model.addAttribute("platillos", platilloRepository.findAll());

        return "roles/admin/crud/platillos/platillos";
    }
}
