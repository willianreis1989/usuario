package com.javanauta.usuario.insfrastructure.repository;


import com.javanauta.usuario.insfrastructure.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    default boolean existisByEmail(String email) {
        return false;
    }

    Optional<Usuario> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);

}
