package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.DetalleCarrito;
import com.proyecto.fragataGiratoria.model.MetodoPago;
import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.service.MetodoPagoService;
import com.proyecto.fragataGiratoria.service.PlatilloService;
import com.proyecto.fragataGiratoria.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/roles/cliente")
@SessionAttributes("carrito")
public class CarritoController {

    @Autowired
    private PlatilloService platilloService;

    @Autowired
    private MetodoPagoService metodoPagoService;

    @Autowired
    private PedidoService pedidoService;

    @ModelAttribute("carrito")
    public Map<Long, DetalleCarrito> inicializarCarrito() {
        return new HashMap<>();
    }

    // ============================================
    // AGREGAR AL CARRITO
    // ============================================
    @PostMapping("/carrito/agregar")
    public String agregarPlatillo(
            @RequestParam Long idPlatillo,
            @RequestParam(defaultValue = "1") Integer cantidad,
            @ModelAttribute("carrito") Map<Long, DetalleCarrito> carrito,
            RedirectAttributes redirectAttributes
    ) {

        Optional<Platillo> platilloOptional = platilloService.obtenerPlatilloPorId(idPlatillo);

        if (platilloOptional.isPresent()) {
            Platillo platillo = platilloOptional.get();

            if (carrito.containsKey(idPlatillo)) {
                DetalleCarrito existente = carrito.get(idPlatillo);
                existente.setCantidad(existente.getCantidad() + cantidad);
            } else {
                carrito.put(idPlatillo, new DetalleCarrito(platillo, cantidad));
            }

            redirectAttributes.addFlashAttribute("agregado", true);
            redirectAttributes.addFlashAttribute("nombrePlatillo", platillo.getNombre());
        }

        return "redirect:/roles/cliente/menu";
    }

    // ============================================
    // VER CARRITO
    // ============================================
    @GetMapping("/carrito")
    public String verCarrito(@ModelAttribute("carrito") Map<Long, DetalleCarrito> carrito,
                             Model model) {

        double total = carrito.values().stream()
                .mapToDouble(DetalleCarrito::getSubtotal)
                .sum();

        List<MetodoPago> metodosPago = metodoPagoService.listarMetodosPago();

        model.addAttribute("itemsCarrito", carrito.values());
        model.addAttribute("total", total);
        model.addAttribute("metodosPago", metodosPago);

        return "roles/cliente/carrito";
    }

    // ============================================
    // REGISTRAR PEDIDO
    // ============================================
    @PostMapping("/carrito/registrar")
    public String registrarPedido(
            @RequestParam("idMetodoPago") Integer idMetodoPago,
            @ModelAttribute("carrito") Map<Long, DetalleCarrito> carrito,
            RedirectAttributes redirectAttributes
    ) {

        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/roles/cliente/carrito";
        }

        pedidoService.registrarPedido(carrito, idMetodoPago);

        carrito.clear();

        redirectAttributes.addFlashAttribute("pedidoExitoso", true);

        return "redirect:/roles/cliente/menu";
    }
}
