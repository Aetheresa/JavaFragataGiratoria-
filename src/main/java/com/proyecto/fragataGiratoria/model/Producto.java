package com.proyecto.fragataGiratoria.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre_producto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "stock_actual")
    private Double stockActual;

    @Column(name = "stock_minimo")
    private Double stockMinimo;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    // Si necesitas calcular cantidad disponible, usa este método:
    public Double getCantidadDisponible() {
        if (stockActual == null || stockMinimo == null) {
            return null;
        }
        return stockActual - stockMinimo;
    }

    public String getTipoProducto() {
        // todo Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTipoProducto'");
    }

    public Date getStockInicial() {
        // todo Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStockInicial'");
    }

    public Date getStockFinal() {
        // todo Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStockFinal'");
    }
}
