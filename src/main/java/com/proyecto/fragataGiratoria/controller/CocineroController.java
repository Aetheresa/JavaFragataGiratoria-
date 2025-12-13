package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import com.proyecto.fragataGiratoria.repository.PedidoDetalleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles/cocinero")
public class CocineroController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    // Mostrar pedidos con sus platillos para cocina
    @GetMapping("/cocina")
    public String verCocina(Model model) {

        List<Pedido> pedidos = pedidoRepository.findAll()
                .stream()
                .filter(p -> !p.getEstadoCocina().equals("LISTO"))
                .toList();

        // Enviar también los detalles asociados
        model.addAttribute("pedidos", pedidos);

        return "roles/cocinero/cocina";
    }

    // Cambiar estado de cocina
    @PostMapping("/estado-cocina/{id}")
    public String actualizarEstadoCocina(@PathVariable Long id,
                                         @RequestParam String estado) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null) {
            pedido.setEstadoCocina(estado);
            pedidoRepository.save(pedido);
        }

        return "redirect:/roles/cocinero/cocina";
    }
}
