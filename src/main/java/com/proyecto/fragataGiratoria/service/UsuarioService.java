package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.RolRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    /* ======================================================
       DEPENDENCIAS (inyección por constructor – buena práctica)
       ====================================================== */
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ======================================================
       CRUD GENERAL
       ====================================================== */

    /**
     * Alias usado por controllers (PedidoController).
     * Evita errores sin romper código existente.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Método original mantenido por compatibilidad.
     */
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public void eliminar(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    /* ======================================================
       REGISTRO DE USUARIOS / CLIENTES
       ====================================================== */

    public Usuario registrarNuevoUsuario(Usuario usuario) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con este correo electrónico."
            );
        }

        Rol rolCliente = rolRepository.findByNombreRol("CLIENTE")
                .orElseThrow(() ->
                        new IllegalArgumentException("No existe el rol CLIENTE en la base de datos.")
                );

        usuario.setRol(rolCliente);

        // Mantiene tu convención actual
        String rawPassword = usuario.getPasswordHash();
        validarContrasenaSegura(rawPassword);

        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    private void validarContrasenaSegura(String password) {
        String regex =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

        if (password == null || !password.matches(regex)) {
            throw new IllegalArgumentException(
                    "La contraseña debe incluir mínimo 8 caracteres, " +
                    "una mayúscula, una minúscula, un número y un carácter especial."
            );
        }
    }

    /* ======================================================
       MÉTODOS PARA LOGIN / AUTH
       ====================================================== */

    public Optional<Usuario> obtenerPorEmailOptional(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        try {
            return usuarioRepository.findByNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
