package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    // ============================================
    // CAMPOS PRINCIPALES
    // ============================================

    @Column(nullable = true)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 255)
    private String observaciones;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    // ============================================
    // CAMPOS DEL SISTEMA
    // ============================================

    @Column(length = 50)
    private String estado;

    @Column(name = "estado_cocina", nullable = false)
    private String estadoCocina;

    @Column(name = "estado_mesero", nullable = false)
    private String estadoMesero;

    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    // ============================================
    // FOREIGN KEYS
    // ============================================

    @Column(name = "id_adicional")
    private Long idAdicional;

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_platillo")
    private Long idPlatillo;

    @Column(name = "id_usuario")
    private Long idUsuario;

    // ============================================
    // RELACIÓN CON DETALLES DE PEDIDO
    // ============================================
    @OneToMany
    @JoinColumn(name = "id_pedido", referencedColumnName = "id_pedido")
    private List<PedidoDetalle> detalles;

    public List<PedidoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }

    // ============================================
    // CONSTRUCTOR
    // ============================================

    public Pedido() {}

    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getEstadoCocina() { return estadoCocina; }
    public void setEstadoCocina(String estadoCocina) { this.estadoCocina = estadoCocina; }

    public String getEstadoMesero() { return estadoMesero; }
    public void setEstadoMesero(String estadoMesero) { this.estadoMesero = estadoMesero; }

    public Integer getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Integer idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public Long getIdAdicional() { return idAdicional; }
    public void setIdAdicional(Long idAdicional) { this.idAdicional = idAdicional; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdPlatillo() { return idPlatillo; }
    public void setIdPlatillo(Long idPlatillo) { this.idPlatillo = idPlatillo; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}
