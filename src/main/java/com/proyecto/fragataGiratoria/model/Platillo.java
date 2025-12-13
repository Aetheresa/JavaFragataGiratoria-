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
 private Long idPlatillo;

@Column(nullable = false, length = 100)
private String nombre;

@Column(nullable = false, length = 500)
private String descripcion;

@Column(nullable = false)
private Double precio;

@Column(nullable = false, length = 100)
private String categoria;

@Column(name = "imagen_url", nullable = false)
private String imagenUrl;

 @Column(name = "emojis", length = 500)
private String emojis;
}