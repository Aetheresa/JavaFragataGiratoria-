package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// La interfaz extiende JpaRepository, indicando la entidad (Pedido) y el tipo de la clave primaria (Long)
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Spring Data JPA provee automáticamente todos los métodos CRUD básicos.
}