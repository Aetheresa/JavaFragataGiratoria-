package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Proveedor; // Asumiendo que existe una entidad Proveedor
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Spring Data JPA proveerá el método count() automáticamente.
}