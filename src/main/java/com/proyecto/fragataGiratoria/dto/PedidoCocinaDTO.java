package com.proyecto.fragataGiratoria.dto;

public class PedidoCocinaDTO {

    private Long idPedido;
    private String nombrePlatillo;
    private int cantidad;
    private String estadoCocina;

    public PedidoCocinaDTO(Long idPedido, String nombrePlatillo, int cantidad, String estadoCocina) {
        this.idPedido = idPedido;
        this.nombrePlatillo = nombrePlatillo;
        this.cantidad = cantidad;
        this.estadoCocina = estadoCocina;
    }

    public Long getIdPedido() { return idPedido; }
    public String getNombrePlatillo() { return nombrePlatillo; }
    public int getCantidad() { return cantidad; }
    public String getEstadoCocina() { return estadoCocina; }
}
