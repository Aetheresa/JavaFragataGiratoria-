package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Método para mostrar los productos
    @GetMapping
    public String mostrarProductos(Model model) {
        // Obtener todos los productos
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);

        // Estadísticas adicionales (opcional)
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("productosBajoStock", productoService.countBajoStock());
        model.addAttribute("totalProveedores", productoService.countProveedores());

        // Retornar la vista de productos, con la ruta correcta
        return "roles/admin/crud/crud_productos/index";  // Asegúrate de que el archivo 'index.html' esté en esta ruta
    }

    // Método para mostrar el formulario de creación de producto
    @GetMapping("/crear")
    public String crearProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "roles/admin/crud/crud_productos/crear"; // Ruta correcta para 'crearProducto.html'
    }

    // Método para guardar un nuevo producto
    @PostMapping("/crear")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/productos"; // Redirige a la lista de productos después de guardar
    }

    // Método para mostrar el formulario de edición de producto
    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id);
        model.addAttribute("producto", producto);
        return "roles/admin/crud/crud_productos/editar"; // Ruta correcta para 'editarProducto.html'
    }

    // Método para actualizar un producto
    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto producto) {
        producto.setIdProducto(id); // Asegúrate de que el ID del producto esté correctamente actualizado
        productoService.save(producto);
        return "redirect:/productos"; // Redirige a la lista de productos después de actualizar
    }

    // Método para eliminar un producto
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.delete(id);
        return "redirect:/productos"; // Redirige a la lista de productos después de eliminar
    }

    // Método para exportar los productos a PDF
    @GetMapping("/export/pdf")
    public String exportarProductosPDF() {
        // TODO: Implementar exportación a PDF
        return "redirect:/productos"; // Redirige a la lista de productos después de la exportación
    }

    // Método para exportar los productos a Excel
    @GetMapping("/export/excel")
    public String exportarProductosExcel() {
        // TODO: Implementar exportación a Excel
        return "redirect:/productos"; // Redirige a la lista de productos después de la exportación
    }
}
