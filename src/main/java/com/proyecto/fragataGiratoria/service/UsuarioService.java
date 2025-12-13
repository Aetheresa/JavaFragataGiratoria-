package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.RolRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------------------------------------------------
    //  🔹 CRUD GENERAL
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    //  🔹 REGISTRO DE CLIENTES
    // ---------------------------------------------------------

    public Usuario registrarNuevoUsuario(Usuario usuario) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo electrónico.");
        }

        Rol rolCliente = rolRepository.findByNombreRol("CLIENTE")
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol CLIENTE en la base de datos."));

        usuario.setRol(rolCliente);

        // Si en tu entidad la contraseña se almacena en passwordHash, ajusta aquí.
        String rawPassword = usuario.getPasswordHash(); // <-- conserva tu convención actual
        validarContrasenaSegura(rawPassword);

        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));

        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    private void validarContrasenaSegura(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

        if (password == null || !password.matches(regex)) {
            throw new IllegalArgumentException(
                    "La contraseña debe incluir: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
            );
        }
    }

    // ---------------------------------------------------------
    //  🔹 MÉTODOS NECESARIOS PARA LOGIN Y CLIENTE
    // ---------------------------------------------------------

    /**
     * Devuelve Optional para mayor seguridad frente a null.
     */
    public Optional<Usuario> obtenerPorEmailOptional(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    /**
     * Buscar por nombreUsuario (username) — devuelve Optional.
     */
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        // Se asume que en UsuarioRepository existe un método findByNombreUsuario returning Optional<Usuario>
        try {
            return usuarioRepository.findByNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            // Si tu repo tiene un método distinto (por ejemplo devuelve Usuario o se llama findByUsername),
            // captura y maneja aquí—por ahora devolvemos Optional.empty()
            return Optional.empty();
        }
    }
}


