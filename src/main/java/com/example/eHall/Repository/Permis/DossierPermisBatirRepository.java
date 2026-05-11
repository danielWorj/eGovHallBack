package com.example.eHall.Repository.Permis;

import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.PermisBatir.DossierPermisBatir;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DossierPermisBatirRepository extends JpaRepository<DossierPermisBatir,Integer> {
    List<DossierPermisBatir> findByDemandeur(Utilisateur utilisateur);
    List<DossierPermisBatir> findByMairie(Mairie mairie);
}
