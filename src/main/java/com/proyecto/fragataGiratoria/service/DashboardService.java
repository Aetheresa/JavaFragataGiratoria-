package com.proyecto.fragataGiratoria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.fragataGiratoria.repository.*;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PlatilloRepository platilloRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Métodos que devuelven conteos (puedes añadir caching si quieres)
    public long countUsuarios() {
        return usuarioRepository.count();
    }

    public long countClientes() {
        return clienteRepository.count();
    }

    public long countPedidos() {
        return pedidoRepository.count();
    }

    public long countPlatillos() {
        return platilloRepository.count();
    }

    public long countMesas() {
        return mesaRepository.count();
    }

    public long countFacturas() {
        return facturaRepository.count();
    }

    public long countCompras() {
        return compraRepository.count();
    }

    public long countProductos() {
        return productoRepository.count();
    }
}
