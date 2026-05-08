package com.example.eHall.Repository.Domaine;

import com.example.eHall.Entity.Domaine.Hopital;
import com.example.eHall.Entity.Domaine.Mairie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HopitalRepository extends JpaRepository<Hopital,Integer> {
    List<Hopital> findByMairie(Mairie mairie);
}
