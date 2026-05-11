package com.example.eHall.Repository.Permis;

import com.example.eHall.Entity.PermisBatir.DossierPermisBatir;
import com.example.eHall.Entity.PermisBatir.PlanExecution;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanExecutionRepository extends JpaRepository<PlanExecution,Integer> {
    List<PlanExecution> findByDossier(DossierPermisBatir dossier);
    @Transactional
    void deleteByDossier(DossierPermisBatir dossier);
}
