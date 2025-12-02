package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
