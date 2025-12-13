package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import com.proyecto.fragataGiratoria.model.DetalleCarrito;

import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import com.proyecto.fragataGiratoria.repository.PedidoDetalleRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         PedidoDetalleRepository pedidoDetalleRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido registrarPedido(Map<Long, DetalleCarrito> carrito, Integer metodoPago) {

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setIdMetodoPago(metodoPago);
        pedido.setEstado("PENDIENTE");
        pedido.setEstadoCocina("PENDIENTE");
        pedido.setEstadoMesero("PENDIENTE");

        BigDecimal total = BigDecimal.ZERO;

        pedido = pedidoRepository.save(pedido);

        for (DetalleCarrito item : carrito.values()) {

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setIdPedido(pedido.getId());
            detalle.setIdPlatillo(item.getPlatillo().getIdPlatillo());
            detalle.setCantidad(item.getCantidad());

            BigDecimal precio = BigDecimal.valueOf(item.getPlatillo().getPrecio());
            detalle.setPrecioUnitario(precio);

            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
            detalle.setSubtotal(subtotal);

            total = total.add(subtotal);

            pedidoDetalleRepository.save(detalle);
        }

        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        return pedido;
    }
}
