package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll(); // Devuelve todos los productos
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto); // Guarda o actualiza el producto
    }

    @Override
    public Producto findById(Long id) {
        return productoRepository.findById(id).orElse(null); // Devuelve el producto por ID, o null si no se encuentra
    }

    @Override
    public void delete(Long id) {
        productoRepository.deleteById(id); // Elimina el producto por ID
    }

    @Override
    public long countBajoStock() {
        // Lógica para contar productos con stock bajo (ejemplo: stock < 10)
        return productoRepository.countByStockActualLessThan(10); 
    }

    @Override
    public long countProveedores() {
        // Lógica para contar proveedores (esto depende de cómo esté modelada tu base de datos)
        return 0; // Aquí deberías devolver el número de proveedores
    }
}
