package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.DetalleCarrito;
import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import com.proyecto.fragataGiratoria.repository.PedidoDetalleRepository;
import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoDetalleRepository detalleRepository;

    // ===============================
    // LISTAR PEDIDOS
    // ===============================
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // ===============================
    // OBTENER PEDIDO POR ID
    // ===============================
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        if (id == null) return Optional.empty();
        return pedidoRepository.findById(id);
    }

    // ===============================
    // GUARDAR / ACTUALIZAR PEDIDO
    // ===============================
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {

        actualizarEstadoGeneral(pedido);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        if (pedido.getDetalles() != null) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                detalle.setPedido(pedidoGuardado);
                detalleRepository.save(detalle);
            }
        }

        return pedidoGuardado;
    }

    // ===============================
    // ELIMINAR PEDIDO
    // ===============================
    public void eliminarPedido(Long id) {
        if (id != null) {
            pedidoRepository.deleteById(id);
        }
    }

    // ===============================
    // REGISTRAR PEDIDO DESDE CARRITO
    // ===============================
    @Transactional
    public void registrarPedido(Map<Long, DetalleCarrito> carrito, Integer idMetodoPago) {

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("PENDIENTE");
        pedido.setEstadoCocina("PENDIENTE");
        pedido.setEstadoMesero("PENDIENTE");
        pedido.setIdMetodoPago(idMetodoPago);

        double total = carrito.values().stream()
                .mapToDouble(DetalleCarrito::getSubtotal)
                .sum();

        pedido.setTotal(BigDecimal.valueOf(total));

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (DetalleCarrito item : carrito.values()) {

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedidoGuardado);
            detalle.setPlatillo(item.getPlatillo());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(
                    BigDecimal.valueOf(item.getPlatillo().getPrecio())
            );
            detalle.setSubtotal(
                    BigDecimal.valueOf(item.getSubtotal())
            );

            detalleRepository.save(detalle);
        }
    }

    // ===============================
    // COCINA MARCA LISTO
    // ===============================
    @Transactional
    public void marcarPedidoListo(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoCocina("LISTO");

        actualizarEstadoGeneral(pedido);

        pedidoRepository.save(pedido);
    }

    // ===============================
    // MESERO ENTREGA PEDIDO
    // ===============================
    @Transactional
    public void marcarPedidoEntregado(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoMesero("ENTREGADO");

        actualizarEstadoGeneral(pedido);

        pedidoRepository.save(pedido);
    }

    // ===============================
    // LÓGICA CENTRAL (NO SE DUPLICA)
    // ===============================
    private void actualizarEstadoGeneral(Pedido pedido) {
        if ("LISTO".equalsIgnoreCase(pedido.getEstadoCocina())
                && "ENTREGADO".equalsIgnoreCase(pedido.getEstadoMesero())) {
            pedido.setEstado("FINALIZADO");
        }
    }
}
