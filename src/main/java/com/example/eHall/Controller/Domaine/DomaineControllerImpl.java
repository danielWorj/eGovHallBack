package com.example.eHall.Controller.Domaine;

import com.example.eHall.Dto.Domaine.HopitalDTO;
import com.example.eHall.Dto.Domaine.MairieDTO;
import com.example.eHall.Entity.Domaine.Hopital;
import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Repository.Domaine.HopitalRepository;
import com.example.eHall.Repository.Domaine.MairieRepository;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Controller
public class DomaineControllerImpl implements DomaineControllerInt {

    @Autowired private MairieRepository  mairieRepository;
    @Autowired private HopitalRepository hopitalRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ═══════════════════════════════════════════════════════════
    //  MAIRIE — CRUD
    // ═══════════════════════════════════════════════════════════

    @Override
    public ResponseEntity<List<Mairie>> findAllMairie() {
        return ResponseEntity.ok(this.mairieRepository.findAll());
    }

    @Override
    public ResponseEntity<Mairie> findMairieById(Integer id) {
        return ResponseEntity.ok(this.mairieRepository.findById(id).orElse(null));
    }

    @Override
    public ResponseEntity<ServerReponse> createMairie(String mairie) {
        try {
            MairieDTO dto = this.objectMapper.readValue(mairie, MairieDTO.class);

            Mairie mairieDB = new Mairie();
            mairieDB.setNom(dto.getNom());
            mairieDB.setEmail(dto.getEmail());
            mairieDB.setTelephone(dto.getTelephone());
            mairieDB.setLocalisation(dto.getLocalisation());
            mairieDB.setCode("MA"+"25");
            mairieDB.setActif(true);
            mairieDB.setCreation(LocalDate.now());


            this.mairieRepository.save(mairieDB);
            return ResponseEntity.ok(new ServerReponse("Mairie créée avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur création mairie : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<ServerReponse> updateMairie(String mairie) {
        try {
            MairieDTO dto = this.objectMapper.readValue(mairie, MairieDTO.class);

            Mairie mairieDB = this.mairieRepository.findById(dto.getId()).orElse(null);
            if (mairieDB == null)
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Mairie introuvable ID : " + dto.getId(), false));

            mairieDB.setNom(dto.getNom());
            mairieDB.setEmail(dto.getEmail());
            mairieDB.setTelephone(dto.getTelephone());
            mairieDB.setLocalisation(dto.getLocalisation());

            this.mairieRepository.save(mairieDB);
            return ResponseEntity.ok(new ServerReponse("Mairie mise à jour avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur mise à jour mairie : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<ServerReponse> deleteMairie(Integer id) {
        try {
            Mairie mairie = this.mairieRepository.findById(id).orElse(null);
            if (mairie == null)
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Mairie introuvable ID : " + id, false));

            // Vérifier qu'aucun hôpital n'est rattaché à cette mairie
            List<Hopital> hopitaux = this.hopitalRepository.findByMairie(mairie);
            if (!hopitaux.isEmpty())
                return ResponseEntity.badRequest().body(new ServerReponse(
                        "Impossible de supprimer : " + hopitaux.size() +
                                " hôpital(aux) rattaché(s) à cette mairie.", false));

            this.mairieRepository.deleteById(id);
            return ResponseEntity.ok(new ServerReponse("Mairie supprimée avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur suppression mairie : " + e.getMessage(), false));
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  HOPITAL — CRUD
    // ═══════════════════════════════════════════════════════════

    @Override
    public ResponseEntity<List<Hopital>> findAllHopital() {
        return ResponseEntity.ok(this.hopitalRepository.findAll());
    }

    @Override
    public ResponseEntity<Hopital> findHopitalById(Integer id) {
        return ResponseEntity.ok(this.hopitalRepository.findById(id).orElse(null));
    }

    @Override
    public ResponseEntity<List<Hopital>> findHopitalByMairie(Integer mairieId) {
        Mairie mairie = this.mairieRepository.findById(mairieId).orElse(null);
        if (mairie == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(this.hopitalRepository.findByMairie(mairie));
    }

    @Override
    public ResponseEntity<ServerReponse> createHopital(String hopital) {
        try {
            HopitalDTO dto = this.objectMapper.readValue(hopital, HopitalDTO.class);

            // Récupération de la mairie — obligatoire
            Mairie mairie = this.mairieRepository.findById(dto.getMairieId()).orElse(null);
            if (mairie == null)
                return ResponseEntity.badRequest().body(
                        new ServerReponse("Mairie introuvable ID : " + dto.getMairieId(), false));

            Hopital hopitalDB = new Hopital();
            hopitalDB.setNom(dto.getNom());
            hopitalDB.setEmail(dto.getEmail());
            hopitalDB.setTelephone(dto.getTelephone());
            hopitalDB.setLocalisation(dto.getLocalisation());
            hopitalDB.setCode("25"+"HOS");
            hopitalDB.setActif(true);
            hopitalDB.setCreation(LocalDate.now());
            hopitalDB.setMairie(mairie); // ← rattachement à la mairie

            this.hopitalRepository.save(hopitalDB);
            return ResponseEntity.ok(new ServerReponse("Hôpital créé avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur création hôpital : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<ServerReponse> updateHopital(String hopital) {
        try {
            HopitalDTO dto = this.objectMapper.readValue(hopital, HopitalDTO.class);

            Hopital hopitalDB = this.hopitalRepository.findById(dto.getId()).orElse(null);
            if (hopitalDB == null)
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Hôpital introuvable ID : " + dto.getId(), false));

            // Mise à jour de la mairie si elle change
            if (dto.getMairieId() != null) {
                Mairie mairie = this.mairieRepository.findById(dto.getMairieId()).orElse(null);
                if (mairie == null)
                    return ResponseEntity.badRequest().body(
                            new ServerReponse("Mairie introuvable ID : " + dto.getMairieId(), false));
                hopitalDB.setMairie(mairie);
            }

            hopitalDB.setNom(dto.getNom());
            hopitalDB.setEmail(dto.getEmail());
            hopitalDB.setTelephone(dto.getTelephone());
            hopitalDB.setLocalisation(dto.getLocalisation());
            hopitalDB.setCode("25"+"HOSP");
            hopitalDB.setActif(true);

            this.hopitalRepository.save(hopitalDB);
            return ResponseEntity.ok(new ServerReponse("Hôpital mis à jour avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur mise à jour hôpital : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<ServerReponse> deleteHopital(Integer id) {
        try {
            Hopital hopital = this.hopitalRepository.findById(id).orElse(null);
            if (hopital == null)
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Hôpital introuvable ID : " + id, false));

            this.hopitalRepository.deleteById(id);
            return ResponseEntity.ok(new ServerReponse("Hôpital supprimé avec succès", true));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur suppression hôpital : " + e.getMessage(), false));
        }
    }
}