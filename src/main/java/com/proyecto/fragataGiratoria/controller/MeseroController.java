package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles/mesero")
public class MeseroController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/mesero")
    public String verMesero(Model model) {

        List<Pedido> pedidos = pedidoRepository.findAll()
                .stream()
                .filter(p -> "LISTO".equals(p.getEstadoCocina()))
                .filter(p -> "PENDIENTE".equals(p.getEstadoMesero()))
                .toList();

        model.addAttribute("pedidos", pedidos);
        return "roles/mesero/mesero";
    }

    @PostMapping("/entregar/{id}")
    public String entregarPedido(@PathVariable Long id) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null) {
            pedido.setEstadoMesero("ENTREGADO");
            pedidoRepository.save(pedido);
        }

        return "redirect:/roles/mesero/mesero";
    }
}
