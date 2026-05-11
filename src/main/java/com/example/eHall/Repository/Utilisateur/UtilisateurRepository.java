package com.example.eHall.Repository.Utilisateur;

import com.example.eHall.Entity.Utilisateur.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Integer> {
    Utilisateur findByEmailAndPassword(String email , String password);
    Optional<Utilisateur> findByNomAndPrenomAndTelephone(String nom, String prenom , String telephone);

}
