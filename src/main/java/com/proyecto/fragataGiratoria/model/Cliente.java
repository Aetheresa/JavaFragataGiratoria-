package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 10)
    private TipoCliente tipo = TipoCliente.LOCAL;

    public enum TipoCliente {
        LOCAL, DELIVERY
    }
}
