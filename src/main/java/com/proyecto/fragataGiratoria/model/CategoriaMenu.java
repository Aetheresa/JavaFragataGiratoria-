package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria_menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre_categoria", nullable = false, length = 100)
    private String nombreCategoria;
}
