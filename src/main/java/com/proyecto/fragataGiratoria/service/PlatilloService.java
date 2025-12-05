package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Platillo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PlatilloService {

    List<Platillo> listarPlatillos();
    Platillo guardarPlatillo(Platillo platillo);
    Optional<Platillo> obtenerPlatilloPorId(Integer id);
    void eliminarPlatillo(Integer id);
    Page<Platillo> listarPlatillosPaginados(Pageable pageable);
    void subirArchivo(MultipartFile file);
}
