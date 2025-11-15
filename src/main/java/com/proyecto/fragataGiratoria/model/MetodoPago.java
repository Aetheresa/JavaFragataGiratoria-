package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "metodo_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre_metodo", nullable = false, length = 50)
    private String nombreMetodo;
}
