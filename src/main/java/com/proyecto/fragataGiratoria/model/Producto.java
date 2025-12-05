package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Producto {

    @Id
    private Long idProducto;  // Identificador único del producto
    private String nombre;    // Nombre del producto
    private String descripcion;  // Descripción del producto
    private String categoria;    // Categoría a la que pertenece
    private Double precioUnitario;  // Precio unitario del producto
    private Integer stockActual;  // Cantidad de stock disponible
    private Integer stockMinimo;  // Stock mínimo requerido
    private String unidadMedida;  // Unidad de medida (ej. piezas, litros, etc.)

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
