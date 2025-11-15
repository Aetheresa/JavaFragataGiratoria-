package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_platillo")
    private Platillo platillo;

    @ManyToOne
    @JoinColumn(name = "id_adicional")
    private Adicional adicional;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private Double total;

    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    private String observaciones;

    public enum EstadoPedido {
        PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO
    }
}
