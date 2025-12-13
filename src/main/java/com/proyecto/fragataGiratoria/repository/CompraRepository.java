package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    // Aquí puedes agregar métodos de búsqueda personalizados si los necesitas.
    // Ejemplo: List<Compra> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}