package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Platillo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatilloRepository extends JpaRepository<Platillo, Integer> {
}
