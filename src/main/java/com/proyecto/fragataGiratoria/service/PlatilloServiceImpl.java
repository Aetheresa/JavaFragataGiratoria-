package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.repository.PlatilloRepository;
import com.proyecto.fragataGiratoria.service.PlatilloService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatilloServiceImpl implements PlatilloService {

    private final PlatilloRepository platilloRepository;

    public PlatilloServiceImpl(PlatilloRepository platilloRepository) {
        this.platilloRepository = platilloRepository;
    }

    @Override
    public List<Platillo> findAll() {
        return platilloRepository.findAll();
    }
}
