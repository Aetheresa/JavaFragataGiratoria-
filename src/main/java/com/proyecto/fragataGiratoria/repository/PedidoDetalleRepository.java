package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

    // Obtener detalles por pedido
    List<PedidoDetalle> findByIdPedido(Long idPedido);
}
