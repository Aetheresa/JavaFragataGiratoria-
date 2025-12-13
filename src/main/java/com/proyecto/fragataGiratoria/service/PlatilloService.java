package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Platillo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PlatilloService {

    /**
     * Devuelve una lista de todos los platillos.
     */
    List<Platillo> listarPlatillos();

    /**
     * Guarda o actualiza un platillo.
     */
    Platillo guardarPlatillo(Platillo platillo);

    /**
     * Obtiene un platillo por su ID.
     * @param id ID del platillo (tipo Long).
     */
    Optional<Platillo> obtenerPlatilloPorId(Long id);

    /**
     * Elimina un platillo por su ID.
     * @param id ID del platillo (tipo Long).
     */
    void eliminarPlatillo(Long id);

    /**
     * Devuelve una lista de platillos paginada.
     */
    Page<Platillo> listarPlatillosPaginados(Pageable pageable);

    /**
     * Sube un archivo (usualmente una imagen).
     */
    void subirArchivo(MultipartFile file);
}
