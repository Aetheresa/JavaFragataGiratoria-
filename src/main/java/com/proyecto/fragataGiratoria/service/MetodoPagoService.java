package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.MetodoPago;
import com.proyecto.fragataGiratoria.repository.MetodoPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    // Inyección de dependencia
    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    /**
     * Obtiene todos los métodos de pago.
     */
    @Transactional(readOnly = true)
    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepository.findAll();
    }
    
    /**
     * Obtiene un método de pago por su ID.
     */
    @Transactional(readOnly = true)
    public Optional<MetodoPago> obtenerMetodoPagoPorId(Integer id) {
        return metodoPagoRepository.findById(id);
    }

    /**
     * Guarda o actualiza un método de pago.
     */
    @Transactional
    public MetodoPago guardar(MetodoPago metodoPago) {
        // Validación ajustada para el nombre_metodo
        if (metodoPago.getNombreMetodo() == null || metodoPago.getNombreMetodo().trim().isEmpty()) {
             throw new IllegalArgumentException("El nombre del método de pago no puede estar vacío.");
        }
        
        return metodoPagoRepository.save(metodoPago);
    }

    /**
     * Elimina un método de pago por su ID.
     */
    @Transactional
    public void eliminar(Integer id) {
        if (!metodoPagoRepository.existsById(id)) {
            throw new IllegalArgumentException("El ID de método de pago proporcionado no existe.");
        }
        metodoPagoRepository.deleteById(id);
    }
}