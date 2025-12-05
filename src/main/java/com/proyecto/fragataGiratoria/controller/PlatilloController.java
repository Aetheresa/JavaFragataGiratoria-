package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.service.PlatilloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/crud/platillos")
public class PlatilloController {

 @Autowired
private PlatilloService platilloService;

 // --- 1. LEER (READ) ---
 
 // Muestra la lista de platillos (platillos.html)
 @GetMapping
public String mostrarPlatillos(Model model) {
List<Platillo> platillos = platilloService.listarPlatillos();
 model.addAttribute("platillos", platillos);
 return "roles/admin/crud/platillos/platillos";
 }

// ------------------------------------------------------------------
 // --- 2. CREAR Y ACTUALIZAR (CREATE & UPDATE) ---
 // ------------------------------------------------------------------

 // Muestra el formulario vacío para un nuevo platillo (nuevo.html)
@GetMapping("/nuevo")
public String mostrarFormularioNuevo(Model model) {
model.addAttribute("platillo", new Platillo());
return "roles/admin/crud/platillos/nuevo";
 }

 // Muestra el formulario de edición con datos precargados
// Corregido el tipo de ID a Integer
 @GetMapping("/editar/{id}")
 public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
 Optional<Platillo> optionalPlatillo = platilloService.obtenerPlatilloPorId(id);

 // CORRECCIÓN CLAVE: Se verifica y extrae el Platillo del Optional
 if (optionalPlatillo.isPresent()) {
 model.addAttribute("platillo", optionalPlatillo.get()); 
 return "roles/admin/crud/platillos/nuevo"; // Usamos la misma plantilla
 } else {
 // Si el platillo no existe, redirige a la lista
return "redirect:/crud/platillos";
 }
 }

 // Maneja el formulario POST: Guarda (si ID es nulo) o Actualiza (si ID existe).
 @PostMapping
public String guardarPlatillo(@ModelAttribute Platillo platillo) {
 platilloService.guardarPlatillo(platillo);
 return "redirect:/crud/platillos";
 }

 // --- 3. ELIMINAR (DELETE) ---

// Ruta para eliminar un platillo por ID
 // Corregido el tipo de ID a Integer
 @GetMapping("/eliminar/{id}")
public String eliminarPlatillo(@PathVariable Integer id) {
 platilloService.eliminarPlatillo(id);
  return "redirect:/crud/platillos"; }
}