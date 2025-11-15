package com.proyecto.fragataGiratoria.config;

import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.RolRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(
            RolRepository rolRepo,
            UsuarioRepository usuarioRepo,
            PasswordEncoder encoder) {

        return args -> {

            // ------------------------
            // CREAR ROLES SI NO EXISTEN
            // ------------------------
            Rol adminRol = rolRepo.findByNombreRol("ADMINISTRADOR")
                    .orElseGet(() -> rolRepo.save(
                            new Rol(null, "ADMINISTRADOR", "Rol para administradores")
                    ));

            Rol meseroRol = rolRepo.findByNombreRol("MESERO")
                    .orElseGet(() -> rolRepo.save(
                            new Rol(null, "MESERO", "Rol para meseros")
                    ));

            Rol cocineroRol = rolRepo.findByNombreRol("COCINERO")
                    .orElseGet(() -> rolRepo.save(
                            new Rol(null, "COCINERO", "Rol para cocina")
                    ));

            Rol clienteRol = rolRepo.findByNombreRol("CLIENTE")
                    .orElseGet(() -> rolRepo.save(
                            new Rol(null, "CLIENTE", "Rol para clientes")
                    ));


            // ------------------------
            // CREAR USUARIOS POR DEFECTO
            // ------------------------

            if (usuarioRepo.findByEmail("admin@fragata.com").isEmpty()) {
                usuarioRepo.save(
                        Usuario.builder()
                                .nombreUsuario("Administrador")
                                .email("admin@fragata.com")
                                .passwordHash(encoder.encode("Admin@2025"))
                                .rol(adminRol)
                                .estado(Usuario.EstadoUsuario.ACTIVO)
                                .fechaCreacion(LocalDateTime.now())
                                .build());
            }

            if (usuarioRepo.findByEmail("mesero@fragata.com").isEmpty()) {
                usuarioRepo.save(
                        Usuario.builder()
                                .nombreUsuario("Mesero")
                                .email("mesero@fragata.com")
                                .passwordHash(encoder.encode("Mesero@2025"))
                                .rol(meseroRol)
                                .estado(Usuario.EstadoUsuario.ACTIVO)
                                .fechaCreacion(LocalDateTime.now())
                                .build());
            }

            if (usuarioRepo.findByEmail("cocinero@fragata.com").isEmpty()) {
                usuarioRepo.save(
                        Usuario.builder()
                                .nombreUsuario("Cocinero")
                                .email("cocinero@fragata.com")
                                .passwordHash(encoder.encode("Cocinero@2025"))
                                .rol(cocineroRol)
                                .estado(Usuario.EstadoUsuario.ACTIVO)
                                .fechaCreacion(LocalDateTime.now())
                                .build());
            }
        };
    }
}

