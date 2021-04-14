package com.etu.montpellier.repository;

import com.etu.montpellier.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByPseudo(String pseudo);

    Boolean existsByPseudo(String pseudo);

    Boolean existsByEmail(String email);
}
