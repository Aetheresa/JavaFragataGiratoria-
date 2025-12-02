package com.proyecto.fragataGiratoria.repository;

import com.proyecto.fragataGiratoria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar usuario por correo electrónico
    Optional<Usuario> findByEmail(String email);
    // Busca un usuario por su campo nombreUsuario (nombre_usuario en la BD)
    Usuario findByNombreUsuario(String nombreUsuario);
}
