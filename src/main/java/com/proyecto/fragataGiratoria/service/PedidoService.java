package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.DetalleCarrito;
import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.model.PedidoDetalle;
import com.proyecto.fragataGiratoria.repository.PedidoDetalleRepository;
import com.proyecto.fragataGiratoria.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PedidoDetalleRepository detalleRepository; // Asumiendo que existe

    // Lista todos los pedidos
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // Obtener un pedido por ID
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        // La advertencia de "Null Type Safety" se ignora aquí, ya que findById retorna Optional
        if (id == null) return Optional.empty();
        return pedidoRepository.findById(id);
    }

    // Guardar o actualizar un pedido (incluyendo detalles)
    @Transactional
    public Pedido guardarPedido(Pedido pedido) {
        // 1. Guardar el pedido principal para obtener el ID si es nuevo
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2. Procesar los detalles del pedido si existen
        if (pedido.getDetalles() != null) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                // CORRECCIÓN CLAVE (para resolver el error de la línea ~70): 
                // Asignamos el objeto Pedido guardado al detalle.
                detalle.setPedido(pedidoGuardado); // <--- CORRECCIÓN

                // Guardar cada detalle
                detalleRepository.save(detalle); 
            }
        }

        return pedidoGuardado;
    }

    // Eliminar un pedido
    public void eliminarPedido(Long id) {
        if (id != null) {
            // La advertencia de "Null Type Safety" se ignora aquí, se asume que el ID no es nulo
            pedidoRepository.deleteById(id);
        }
    }

    public void registrarPedido(Map<Long,DetalleCarrito> carrito, Integer idMetodoPago) {
        // todo Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registrarPedido'");
    }
}