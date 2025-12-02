package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {
}
