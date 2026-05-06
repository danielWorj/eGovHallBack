package com.example.eHall.Repository.Domaine;

import com.example.eHall.Entity.Domaine.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StructureRepository extends JpaRepository<Etablissement,Integer> {

}
