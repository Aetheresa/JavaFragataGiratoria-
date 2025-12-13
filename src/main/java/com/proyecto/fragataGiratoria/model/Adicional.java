package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "adicionales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adicional")
    private Integer idAdicional;

    @Column(name = "nombre_adicional", nullable = false, length = 100)
    private String nombreAdicional;

    // CORRECCIÓN: Se añade @Builder.Default
    @Builder.Default
    @Column(name = "precio", nullable = false)
    private Double precio = 0.0;
}