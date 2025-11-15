package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    // Devuelve todos los roles
    public List<Rol> obtenerRoles() {
        return rolRepository.findAll();
    }

    // Devuelve un rol por nombre
    public Optional<Rol> obtenerPorNombre(String nombreRol) {
        return rolRepository.findByNombreRol(nombreRol);
    }
}
