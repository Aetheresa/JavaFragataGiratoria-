package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public void eliminarProducto(Integer id) {
        productoRepository.deleteById(id);
    }
}
