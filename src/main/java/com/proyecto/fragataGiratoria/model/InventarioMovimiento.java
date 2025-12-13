package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimiento tipo;

    @Column(name = "cantidad", nullable = false)
    private Double cantidad;

    // CORRECTION: Added @Builder.Default to initialize the field correctly
    @Builder.Default
    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "descripcion")
    private String descripcion;

    public enum TipoMovimiento {
        ENTRADA, SALIDA
    }
}