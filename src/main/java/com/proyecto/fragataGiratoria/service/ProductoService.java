package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {

    List<Producto> listarProductos();
    Producto guardarProducto(Producto producto);
    Optional<Producto> obtenerProductoPorId(Integer id);
    void eliminarProducto(Integer id);
}
