package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
    
    // Métodos específicos se añadirían aquí si fueran necesarios.
}