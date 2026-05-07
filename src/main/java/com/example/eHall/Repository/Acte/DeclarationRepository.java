package com.example.eHall.Repository.Acte;

import com.example.eHall.Entity.Acte.Declaration;
import com.example.eHall.Entity.Domaine.Etablissement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeclarationRepository extends JpaRepository<Declaration,Integer> {
    List<Declaration> findByStructure(Etablissement structure);
}
