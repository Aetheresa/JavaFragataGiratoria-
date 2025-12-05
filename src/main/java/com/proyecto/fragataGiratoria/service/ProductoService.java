package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import java.util.List;

public interface ProductoService {

    // Obtener todos los productos
    List<Producto> findAll();

    // Guardar o actualizar un producto
    Producto save(Producto producto);

    // Obtener producto por ID
    Producto findById(Long id);

    // Eliminar un producto
    void delete(Long id);

    // Contar productos bajos de stock
    long countBajoStock();

    // Contar proveedores
    long countProveedores();
}
