package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.repository.CompraRepository;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @GetMapping("/roles/admin/dashboard")
    public String dashboardAdmin(Model model) {

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("content", "roles/admin/inicio :: content");

        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalCompras", compraRepository.count());

        return "roles/admin/dashboard";
    }

    @GetMapping("/roles/mesero/dashboard")
    public String dashboardMesero(Model model) {
        return "roles/mesero/dashboard";
    }

    @GetMapping("/roles/cocinero/dashboard")
    public String dashboardCocina(Model model) {
        return "roles/cocinero/dashboard";
    }
}
