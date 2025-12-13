package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado de idCompra a id para simpleza en el mapping

    private String descripcion;
    
    private LocalDate fecha; // Usamos LocalDate para manejar solo la fecha sin hora
    
    private BigDecimal total; // Usamos BigDecimal para manejar dinero con precisión

    // Constructor vacío (necesario para JPA)
    public Compra() {}

    // Constructor con parámetros (opcional)
    public Compra(String descripcion, LocalDate fecha, BigDecimal total) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.total = total;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}