package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
}
