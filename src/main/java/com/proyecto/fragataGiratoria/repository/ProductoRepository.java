package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("""
           SELECT p 
           FROM Producto p 
           WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(p.categoria) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :search, '%'))
           """)
    List<Producto> buscar(@Param("search") String search);

    // 🔥 Cuenta los productos activos
    long countByActivoTrue();
}
