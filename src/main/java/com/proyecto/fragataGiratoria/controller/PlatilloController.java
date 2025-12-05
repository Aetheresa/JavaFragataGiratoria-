package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.service.PlatilloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/crud/platillos")
public class PlatilloController {

    @Autowired
    private PlatilloService platilloService;

    // Mostrar todos los platillos
    @GetMapping
    public String mostrarPlatillos(Model model) {
        List<Platillo> platillos = platilloService.listarPlatillos();
        model.addAttribute("platillos", platillos);
        return "roles/admin/crud/platillos/platillos";
    }

    // Formulario para crear un nuevo platillo
    @GetMapping("/nuevo")
    public String nuevoPlatillo(Model model) {
        Platillo platillo = new Platillo();
        platillo.setCategoria(""); // Inicializamos como String vacío
        model.addAttribute("platillo", platillo);
        return "roles/admin/crud/platillos/nuevo";
    }

    // Guardar platillo
    @PostMapping("/guardar")
    public String guardarPlatillo(@ModelAttribute Platillo platillo) {
        platilloService.guardarPlatillo(platillo);
        return "redirect:/crud/platillos";
    }

    // Exportar PDF
    @GetMapping("/export/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=platillos.pdf");

        List<Platillo> platillos = platilloService.listarPlatillos();

        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("Lista de Platillos\n\n");
        for (Platillo p : platillos) {
            pdfContent.append("ID: ").append(p.getIdPlatillo())
                      .append(", Nombre: ").append(p.getNombre())
                      .append(", Precio: ").append(p.getPrecio())
                      .append("\n");
        }

        response.getOutputStream().write(pdfContent.toString().getBytes());
        response.getOutputStream().flush();
    }

    // Exportar Excel (CSV simple)
    @GetMapping("/export/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=platillos.xlsx");

        List<Platillo> platillos = platilloService.listarPlatillos();

        StringBuilder excelContent = new StringBuilder();
        excelContent.append("ID,Nombre,Precio,Categoria,Descripcion,Imagen URL,Emojis\n");
        for (Platillo p : platillos) {
            excelContent.append(p.getIdPlatillo())
                        .append(",").append(p.getNombre())
                        .append(",").append(p.getPrecio())
                        .append(",").append(p.getCategoria())
                        .append(",").append(p.getDescripcion())
                        .append(",").append(p.getImagenUrl())
                        .append(",").append(p.getEmojis())
                        .append("\n");
        }

        response.getOutputStream().write(excelContent.toString().getBytes());
        response.getOutputStream().flush();
    }
}
