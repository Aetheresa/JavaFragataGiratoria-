package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Platillo;
import org.springframework.data.jpa.repository.JpaRepository; 
// O CrudRepository, si es el que usas

// Corrección CRUCIAL: Cambiar el segundo parámetro de Integer a Long
public interface PlatilloRepository extends JpaRepository<Platillo, Long> {
    
    // Spring Data JPA ahora usará Long para findById, deleteById, etc.

}