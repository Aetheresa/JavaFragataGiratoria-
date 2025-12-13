package com.proyecto.fragataGiratoria.model;

public class DetalleCarrito {

    private Platillo platillo;
    private Integer cantidad;
    private Double subtotal;

    public DetalleCarrito(Platillo platillo, Integer cantidad) {
        this.platillo = platillo;
        this.cantidad = cantidad;
        this.subtotal = platillo.getPrecio() * cantidad;
    }

    public Platillo getPlatillo() {
        return platillo;
    }

    public void setPlatillo(Platillo platillo) {
        this.platillo = platillo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.platillo.getPrecio() * cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}
