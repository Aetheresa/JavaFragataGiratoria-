package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Producto;  // Importa la clase Producto correctamente

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    long countByStockActualLessThan(int stockMinimo);
    // Aquí puedes agregar métodos adicionales para consultas personalizadas si es necesario
}
