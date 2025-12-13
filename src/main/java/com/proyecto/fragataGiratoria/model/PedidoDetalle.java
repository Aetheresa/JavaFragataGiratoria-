package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_detalle")
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;

    // ELIMINAMOS O HACEMOS QUE LA RELACIÓN GESTIONE EL ID:
    /* @Column(name = "id_pedido", nullable = false)
    private Long idPedido; */

    @Column(name = "id_platillo", insertable = false, updatable = false, nullable = false)
    private Long idPlatillo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    // ============================================
    // CORRECCIÓN CLAVE: ESTE CAMPO DEBE LLAMARSE EXACTAMENTE 'pedido'
    // PARA COINCIDIR CON mappedBy="pedido" EN Pedido.java
    // ============================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido; // <--- ¡DEBE SER 'pedido'!

    // ============================================
    // RELACIÓN CON PLATILLO
    // ============================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_platillo", insertable = false, updatable = false)
    private Platillo platillo;

    public PedidoDetalle() {}

    // ... (El resto de Getters y Setters)

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; } 
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdPlatillo() { return idPlatillo; }
    public void setIdPlatillo(Long idPlatillo) { this.idPlatillo = idPlatillo; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public Platillo getPlatillo() { return platillo; }
    public void setPlatillo(Platillo platillo) { this.platillo = platillo; }
}