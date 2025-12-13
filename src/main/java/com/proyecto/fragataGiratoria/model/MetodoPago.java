package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "metodos_pago")
@Data // Incluye @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class MetodoPago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre_metodo", nullable = false, length = 50)
    private String nombreMetodo; // <-- NOMBRE DEL CAMPO AJUSTADO
}