package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido") // <-- CORRECCIÓN: Mapea a la columna real de la DB
    private Integer id; 

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha = LocalDate.now();
    
    // Campos que generalmente son temporales o de resumen
    private Integer cantidad; 
    private Double precioUnitario; 
    private BigDecimal subtotal; 
    private BigDecimal total;    

    // Estados
    private String estado;
    private String estadoCocina;
    private String estadoMesero;
    private String observaciones;

    // Campos planos para mapeo temporal o desde PedidoDetalle
    private Integer idPlatillo; 
    private String nombrePlatillo; // <-- Necesario para el Controller (Error 7, 13, 14)
    private Integer idMetodoPago;
    private Integer idAdicional;

    // --- Relaciones ManyToOne (Objetos completos) ---
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente; // <-- Necesario para el Controller (Error 1, 2, 3, 15, 16)
    
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // <-- Necesario para el Controller (Error 4, 5, 6, 10, 11, 12)
    
    // --- Relaciones OneToMany (Detalles) ---
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles; 

    // --- CONSTRUCTORES ---
    public Pedido() {
    }

    // --- GETTERS Y SETTERS (TODOS) ---
    // Si su IDE genera Getters/Setters automáticamente, debe generarlos para CADA campo.

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
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
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getIdPlatillo() { return idPlatillo; }
    public void setIdPlatillo(Integer idPlatillo) { this.idPlatillo = idPlatillo; }
    
    // GETTERS Y SETTERS REQUERIDOS POR EL CONTROLLER:
    public String getNombrePlatillo() { return nombrePlatillo; }
    public void setNombrePlatillo(String nombrePlatillo) { this.nombrePlatillo = nombrePlatillo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Integer getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Integer idMetodoPago) { this.idMetodoPago = idMetodoPago; }
    public Integer getIdAdicional() { return idAdicional; }
    public void setIdAdicional(Integer idAdicional) { this.idAdicional = idAdicional; }
    public List<PedidoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<PedidoDetalle> detalles) { this.detalles = detalles; }
}