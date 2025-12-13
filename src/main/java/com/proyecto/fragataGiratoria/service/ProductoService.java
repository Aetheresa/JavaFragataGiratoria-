package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    
    List<Producto> findAll();
    
    Producto save(@NonNull Producto producto);
    
    Optional<Producto> findById(@NonNull Long id);
    
    void delete(@NonNull Long id);
    
    long countBajoStock();
    
    long countProveedores();
}