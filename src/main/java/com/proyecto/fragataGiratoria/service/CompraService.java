package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Compra;
import com.proyecto.fragataGiratoria.repository.CompraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;

    // Inyección de dependencias del repositorio
    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    // --- 1. LEER (READ) ---
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }
    
    public Optional<Compra> obtenerCompraPorId(Long id) {
        return compraRepository.findById(id);
    }

    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    public Compra guardarCompra(Compra compra) {
        // Si el objeto 'compra' tiene un ID, JPA lo actualiza. 
        // Si no tiene ID (es nulo), JPA lo inserta.
        return compraRepository.save(compra);
    }

    // --- 3. ELIMINAR (DELETE) ---
    public void eliminarCompra(Long id) {
        compraRepository.deleteById(id);
    }
}