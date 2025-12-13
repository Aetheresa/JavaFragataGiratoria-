package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import com.proyecto.fragataGiratoria.repository.ProveedorRepository; 
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
import jakarta.persistence.EntityNotFoundException; 
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, 
                               ProveedorRepository proveedorRepository) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto save(@NonNull Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Optional<Producto> findById(@NonNull Long id) {
        return productoRepository.findById(id); 
    }

    @Override
    public void delete(@NonNull Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Producto no encontrado.");
        }
        productoRepository.deleteById(id);
    }

    @Override
    public long countBajoStock() {
        int umbralStock = 10; 
        return productoRepository.countByStockActualLessThan(umbralStock);
    }

    @Override
    public long countProveedores() {
        return proveedorRepository.count(); 
    }
}