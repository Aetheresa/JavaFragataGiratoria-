package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}
