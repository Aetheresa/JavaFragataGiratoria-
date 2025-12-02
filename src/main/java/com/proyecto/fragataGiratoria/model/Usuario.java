package com.proyecto.fragataGiratoria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // CORRECCIÓN: Se añade @Builder.Default (Línea 37)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 15)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    // CORRECCIÓN: Se añade @Builder.Default (Línea 40)
    @Builder.Default
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum EstadoUsuario {
        ACTIVO, INACTIVO, SUSPENDIDO
    }
}