package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.repository.PlatilloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class PlatilloServiceImpl implements PlatilloService {

    @Autowired
    private PlatilloRepository platilloRepository;

    @Override
    public List<Platillo> listarPlatillos() {
        return platilloRepository.findAll();
    }

    @Override
    // CAMBIADO: Ahora recibe Long
    public Optional<Platillo> obtenerPlatilloPorId(Long id) { 
        if (id == null) return Optional.empty();
        // El repositorio de JPA generalmente usa el mismo tipo de ID que el service.
        // Si el repositorio usa Long, esto funciona directamente.
        return platilloRepository.findById(id); 
    }

    @Override
    public Platillo guardarPlatillo(Platillo platillo) {
        return platilloRepository.save(platillo);
    }

    @Override
    // CAMBIADO: Ahora recibe Long
    public void eliminarPlatillo(Long id) { 
        if (id != null) {
            platilloRepository.deleteById(id);
        }
    }

    @Override
    public Page<Platillo> listarPlatillosPaginados(Pageable pageable) {
        return platilloRepository.findAll(pageable);
    }

    @Override
    public void subirArchivo(MultipartFile file) {
        // ... (Implementación de subirArchivo omitida por simplicidad, se mantiene igual)
        if (file.isEmpty()) {
            throw new RuntimeException("No se ha seleccionado ningún archivo.");
        }
        try {
            Path path = Paths.get("uploads/" + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            System.out.println("Archivo subido a: " + path.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error al subir el archivo", e);
        }
    }
}