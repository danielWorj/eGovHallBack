package com.example.eHall.Controller.Permis;

import com.example.eHall.Entity.PermisBatir.DossierPermisBatir;
import com.example.eHall.Entity.PermisBatir.PlanExecution;
import com.example.eHall.Entity.PermisBatir.TypePlanExecution;
import com.example.eHall.Entity.Server.ServerReponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/eHall/api/permis")
@CrossOrigin("*")
public interface PermisControllerInt {

    // ── Lecture ──────────────────────────────────────────────────────────

    @GetMapping("/dossier/all")
    ResponseEntity<List<DossierPermisBatir>> findAll();

    @GetMapping("/dossier/allbymairie/{id}")
    ResponseEntity<List<DossierPermisBatir>> findAllByMairie(@PathVariable Integer id);

    @GetMapping("/dossier/allbyuser/{id}")
    ResponseEntity<List<DossierPermisBatir>> findAllByUser(@PathVariable Integer id);

    @GetMapping("/dossier/byId/{id}")
    ResponseEntity<DossierPermisBatir> findById(@PathVariable Integer id);

    // ── Création ─────────────────────────────────────────────────────────
    // Fichiers attendus dans le multipart (chaque @RequestPart correspond à un champ) :
    //   "dossier"             → JSON DossierPermisDto
    //   "demandeTimbre"       → fichier unique
    //   "certificatUrbanisme" → fichier unique
    //   "certificatPropriete" → fichier unique
    //   "devis"               → fichier unique
    //   "planMasse"           → fichier unique
    //   "planSituationTerrain"→ fichier unique
    //   "cni"                 → fichier unique (photocopie CNI)
    //   "plansExecution"      → tableau de fichiers (plusieurs plans)

    @PostMapping(value = "/dossier/create", consumes = "multipart/form-data")
    ResponseEntity<ServerReponse> createDossierPermis(
            @RequestPart("dossier")                                    String              dossier,
            @RequestPart(value = "demandeTimbre",        required = false) MultipartFile  demandeTimbre,
            @RequestPart(value = "certificatUrbanisme",  required = false) MultipartFile  certificatUrbanisme,
            @RequestPart(value = "certificatPropriete",  required = false) MultipartFile  certificatPropriete,
            @RequestPart(value = "devis",                required = false) MultipartFile  devis,
            @RequestPart(value = "planMasse",            required = false) MultipartFile  planMasse,
            @RequestPart(value = "planSituationTerrain", required = false) MultipartFile  planSituationTerrain,
            @RequestPart(value = "cni",                  required = false) MultipartFile  cni,
            @RequestPart(value = "plansExecution",       required = false) MultipartFile[] plansExecution
    ) throws Exception;

    // ── Mise à jour ───────────────────────────────────────────────────────

    @PutMapping(value = "/dossier/update/{id}", consumes = "multipart/form-data")
    ResponseEntity<ServerReponse> updateDossierPermis(
            @PathVariable                                               Integer             id,
            @RequestPart("dossier")                                    String              dossier,
            @RequestPart(value = "demandeTimbre",        required = false) MultipartFile  demandeTimbre,
            @RequestPart(value = "certificatUrbanisme",  required = false) MultipartFile  certificatUrbanisme,
            @RequestPart(value = "certificatPropriete",  required = false) MultipartFile  certificatPropriete,
            @RequestPart(value = "devis",                required = false) MultipartFile  devis,
            @RequestPart(value = "planMasse",            required = false) MultipartFile  planMasse,
            @RequestPart(value = "planSituationTerrain", required = false) MultipartFile  planSituationTerrain,
            @RequestPart(value = "cni",                  required = false) MultipartFile  cni,
            @RequestPart(value = "plansExecution",       required = false) MultipartFile[] plansExecution
    );

    // ── Suppression ───────────────────────────────────────────────────────

    @DeleteMapping("/dossier/delete/{id}")
    ResponseEntity<ServerReponse> deleteDossierPermis(@PathVariable Integer id);

    //PLAN D'EXECEUTION

    @GetMapping("/planExecution/bydossier/{id}")
    ResponseEntity<List<PlanExecution>> getAllPlanExecutionByDossier(@PathVariable Integer id);


    //Type Plan
    @GetMapping("/typeplan/all")
    ResponseEntity<List<TypePlanExecution>> getAlLPlanExecution();
}