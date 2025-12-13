package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

    /**
     * Busca los detalles de un pedido usando la relación 'pedido' y su ID.
     * Esto corrige el error de "No property 'pedido' found".
     */
    List<PedidoDetalle> findByPedidoId(Long id); // <-- Método corregido
    
    // Si realmente necesita el método para buscar por el ID de la columna,
    // que ahora está mapeada por el objeto Pedido, puede usar una consulta JPQL:
    // @Query("SELECT d FROM PedidoDetalle d WHERE d.pedido.id = :idPedido")
    // List<PedidoDetalle> findDetallesPorIdPedidoJPQL(@Param("idPedido") Long idPedido);
}