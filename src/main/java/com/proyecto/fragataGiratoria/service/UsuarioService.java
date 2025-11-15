package com.proyecto.fragataGiratoria.service;

import com.proyecto.fragataGiratoria.model.Rol;
import com.proyecto.fragataGiratoria.model.Usuario;
import com.proyecto.fragataGiratoria.repository.RolRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository;
import com.proyecto.fragataGiratoria.config.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

        // 1️⃣ Validar duplicado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo electrónico.");
        }

        // 2️⃣ Asignar rol CLIENTE
        Rol rolCliente = rolRepository.findByNombreRol("CLIENTE")
                .orElseThrow(() -> new IllegalArgumentException("No existe el rol CLIENTE en la base de datos."));

        usuario.setRol(rolCliente);

        // 3️⃣ Validar contraseña segura
        validarContrasenaSegura(usuario.getPasswordHash());

        // 4️⃣ Encriptar
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        // 5️⃣ Estado + fecha
        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setFechaCreacion(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }


    private void validarContrasenaSegura(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";

        if (!password.matches(regex)) {
            throw new IllegalArgumentException(
                "La contraseña debe incluir: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial."
            );
        }
    }


    // ---------------------------------------------------------
    //  🔹 MÉTODOS NECESARIOS PARA LOGIN Y CLIENTE
    // ---------------------------------------------------------

    // Obtener usuario por email (lo usa Security y el controlador de cliente)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    // Obtener usuario autenticado
    public Usuario obtenerUsuarioAutenticado(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return null;

        return obtenerPorEmail(userDetails.getUsername()); // username = email
    }
}
