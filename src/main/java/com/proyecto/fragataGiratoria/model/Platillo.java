package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "platillo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Platillo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_platillo")
    private Integer idPlatillo;

    @Column(name = "nombre_platillo", nullable = false, length = 100)
    private String nombrePlatillo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaMenu categoria;
}
