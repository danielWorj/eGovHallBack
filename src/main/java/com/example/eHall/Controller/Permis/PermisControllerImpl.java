package com.example.eHall.Controller.Permis;

import com.example.eHall.Dto.Permis.DossierPermisDto;
import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.PermisBatir.DossierPermisBatir;
import com.example.eHall.Entity.PermisBatir.PlanExecution;
import com.example.eHall.Entity.PermisBatir.StatutDossier;
import com.example.eHall.Entity.PermisBatir.TypePlanExecution;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.example.eHall.Repository.Domaine.MairieRepository;
import com.example.eHall.Repository.Permis.DossierPermisBatirRepository;
import com.example.eHall.Repository.Permis.PlanExecutionRepository;
import com.example.eHall.Repository.Permis.StatutDossierRepository;
import com.example.eHall.Repository.Permis.TypePlanExecutionRepository;
import com.example.eHall.Repository.Utilisateur.UtilisateurRepository;
import com.example.eHall.Service.CloudinaryService;
import com.example.eHall.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class PermisControllerImpl implements PermisControllerInt {

    // ── Logger ────────────────────────────────────────────────────────────
    private static final Logger log = LoggerFactory.getLogger(PermisControllerImpl.class);

    @Autowired private DossierPermisBatirRepository dossierRepository;
    @Autowired private PlanExecutionRepository       planExecutionRepository;
    @Autowired private StatutDossierRepository       statutDossierRepository;
    @Autowired private TypePlanExecutionRepository   typePlanExecutionRepository;
    @Autowired private MairieRepository              mairieRepository;
    @Autowired private UtilisateurRepository         utilisateurRepository;

    @Autowired private CloudinaryService             cloudinaryService;
    @Autowired private EmailService                  emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── findAll ───────────────────────────────────────────────────────────
    @Override
    public ResponseEntity<List<DossierPermisBatir>> findAll() {
        log.info("GET /dossier/all");
        return ResponseEntity.ok(this.dossierRepository.findAll());
    }

    // ── findAllByMairie ───────────────────────────────────────────────────
    @Override
    public ResponseEntity<List<DossierPermisBatir>> findAllByMairie(Integer id) {
        log.info("GET /dossier/allbymairie/{}", id);
        Mairie mairie = this.mairieRepository.findById(id).orElse(null);
        if (mairie == null) {
            log.warn("findAllByMairie → mairie introuvable, id={}", id);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(this.dossierRepository.findByMairie(mairie));
    }

    // ── findAllByUser ─────────────────────────────────────────────────────
    @Override
    public ResponseEntity<List<DossierPermisBatir>> findAllByUser(Integer id) {
        log.info("GET /dossier/allbyuser/{}", id);
        Utilisateur utilisateur = this.utilisateurRepository.findById(id).orElse(null);
        if (utilisateur == null) {
            log.warn("findAllByUser → utilisateur introuvable, id={}", id);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(this.dossierRepository.findByDemandeur(utilisateur));
    }

    // ── findById ──────────────────────────────────────────────────────────
    @Override
    public ResponseEntity<DossierPermisBatir> findById(Integer id) {
        log.info("GET /dossier/byId/{}", id);
        DossierPermisBatir dossier = this.dossierRepository.findById(id).orElse(null);
        if (dossier == null) {
            log.warn("findById → dossier introuvable, id={}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dossier);
    }

    // ── createDossierPermis ───────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> createDossierPermis(
            String          dossierJson,
            MultipartFile   demandeTimbre,
            MultipartFile   certificatUrbanisme,
            MultipartFile   certificatPropriete,
            MultipartFile   devis,
            MultipartFile   planMasse,
            MultipartFile   planSituationTerrain,
            MultipartFile   cni,
            MultipartFile[] plansExecution) throws Exception {

        log.info("POST /dossier/create → début création dossier permis");

            // 1. Désérialisation du DTO
            DossierPermisDto dto = this.objectMapper.readValue(dossierJson, DossierPermisDto.class);
            log.debug("createDossierPermis → DTO désérialisé : nom={}, prénom={}", dto.getNom(), dto.getPrenom());

            // 2. Recherche du demandeur — si inexistant on le crée
            Utilisateur demandeur = this.utilisateurRepository
                    .findByNomAndPrenomAndTelephone(dto.getNom(), dto.getPrenom(), dto.getTelephone())
                    .orElse(null);

            if (demandeur == null) {
                log.info("createDossierPermis → demandeur inexistant, création en base");
                demandeur = new Utilisateur();
                demandeur.setNom(dto.getNom());
                demandeur.setPrenom(dto.getPrenom());
                demandeur.setTelephone(dto.getTelephone());
                demandeur.setEmail(dto.getEmail());
                demandeur.setCreation(LocalDate.now());
                demandeur.setModification(LocalDate.now());
                demandeur.setPassword("PB-" + dto.getNom() + dto.getPrenom() + "XXX");
                demandeur = this.utilisateurRepository.save(demandeur);
            }

            // 3. Vérification de la mairie
            Mairie mairie = this.mairieRepository.findById(dto.getMairieId()).orElse(null);
            if (mairie == null) {
                log.warn("createDossierPermis → mairie introuvable, id={}", dto.getMairieId());
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Mairie introuvable — ID : " + dto.getMairieId(), false));
            }

            // 4. Statut initial du dossier (id=1 → SOUMIS)
            StatutDossier statutSoumis = this.statutDossierRepository.findById(1).orElse(null);
            if (statutSoumis == null) {
                log.error("createDossierPermis → statut SOUMIS (id=1) absent de la base !");
                return ResponseEntity.internalServerError()
                        .body(new ServerReponse("Statut SOUMIS introuvable en base", false));
            }

            // 5. Génération du numéro de dossier
            String numeroDossier = "PB-" + LocalDate.now().getYear()
                    + "-" + mairie.getId()
                    + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // 6. Construction de l'entité dossier
            DossierPermisBatir dossier = new DossierPermisBatir();
            dossier.setNumeroDossier(numeroDossier);
            dossier.setDate(LocalDate.now());
            dossier.setStatut(statutSoumis);
            dossier.setDemandeur(demandeur);
            dossier.setMairie(mairie);
            dossier.setRaison(dto.getRaison());
            dossier.setDateInstruction(LocalDate.now().plusDays(45));

            // 7. Upload des pièces justificatives sur Cloudinary
            if (demandeTimbre        != null && !demandeTimbre.isEmpty())
                dossier.setDemandeTimbre(uploadFichier(demandeTimbre));
            if (certificatUrbanisme  != null && !certificatUrbanisme.isEmpty())
                dossier.setCertificatUrbanisme(uploadFichier(certificatUrbanisme));
            if (certificatPropriete  != null && !certificatPropriete.isEmpty())
                dossier.setCertificatPropriete(uploadFichier(certificatPropriete));
            if (devis                != null && !devis.isEmpty())
                dossier.setDevis(uploadFichier(devis));
            if (planMasse            != null && !planMasse.isEmpty())
                dossier.setPlanMasse(uploadFichier(planMasse));
            if (planSituationTerrain != null && !planSituationTerrain.isEmpty())
                dossier.setPlanTerrain(uploadFichier(planSituationTerrain));
            if (cni                  != null && !cni.isEmpty())
                dossier.setCni(uploadFichier(cni));

            // 8. Sauvegarde du dossier
            DossierPermisBatir dossierSaved = this.dossierRepository.save(dossier);
            log.info("createDossierPermis → dossier sauvegardé, numéro={}", numeroDossier);

            // 9. Upload et sauvegarde des plans d'exécution
            if (plansExecution != null && plansExecution.length > 0) {
                List<Integer> typesIds = dto.getTypesPlansIds();
                if (typesIds == null || typesIds.size() != plansExecution.length) {
                    log.warn("createDossierPermis → incohérence plans : {} types vs {} fichiers",
                            typesIds == null ? 0 : typesIds.size(), plansExecution.length);
                    return ResponseEntity.badRequest().body(new ServerReponse(
                            "Nombre de types de plans (" + (typesIds == null ? 0 : typesIds.size())
                                    + ") != nombre de fichiers plans (" + plansExecution.length + ")", false));
                }

                List<PlanExecution> plans = new ArrayList<>();
                for (int i = 0; i < plansExecution.length; i++) {
                    if (plansExecution[i].isEmpty()) continue;

                    TypePlanExecution typePlan = this.typePlanExecutionRepository
                            .findById(typesIds.get(i)).orElse(null);
                    if (typePlan == null) {
                        log.warn("createDossierPermis → TypePlanExecution introuvable, id={}", typesIds.get(i));
                        return ResponseEntity.badRequest().body(new ServerReponse(
                                "TypePlanExecution introuvable — ID : " + typesIds.get(i), false));
                    }

                    PlanExecution plan = new PlanExecution();
                    plan.setDossier(dossierSaved);
                    plan.setTypePlan(typePlan);
                    plan.setChemin(uploadFichier(plansExecution[i]));
                    plans.add(plan);
                }
                this.planExecutionRepository.saveAll(plans);
                log.info("createDossierPermis → {} plan(s) d'exécution sauvegardé(s)", plans.size());
            }

            // 10. Email de confirmation au demandeur
            String dateLimite = dossierSaved.getDateInstruction().getDayOfMonth() + "/"
                    + dossierSaved.getDateInstruction().getMonthValue() + "/"
                    + dossierSaved.getDateInstruction().getYear();

            String corpsEmail =
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "  Numéro de dossier : " + numeroDossier + "\n" +
                            "  Date de dépôt     : " + LocalDate.now() + "\n" +
                            "  Mairie compétente : " + mairie.getNom() + "\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "Date limite d'instruction : " + dateLimite + " (45 jours)\n\n" +
                            "Votre dossier est en cours d'examen par la commission compétente.\n" +
                            "Vous serez notifié(e) par email dès qu'une décision sera rendue.\n\n" +
                            "Conservez précieusement votre numéro de dossier pour tout suivi.\n\n" +
                            "Cordialement,\nL'équipe eHall";

            this.emailService.sendSimpleEmail(
                    demandeur.getEmail(),
                    "Dossier permis de bâtir reçu — N° " + numeroDossier,
                    "Bonjour " + demandeur.getPrenom() + " " + demandeur.getNom() + ",\n\n" +
                            "Votre demande de permis de bâtir a été reçue avec succès.\n\n" + corpsEmail
            );

            //log.info("createDossierPermis → email envoyé à {}", demandeur.getEmail());
            return ResponseEntity.ok(new ServerReponse(
                    "CREATION DOSSIER PERMIS : SUCCESS — N° " + numeroDossier, true));

    }

    // ── updateDossierPermis ───────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> updateDossierPermis(
            Integer         id,
            String          dossierJson,
            MultipartFile   demandeTimbre,
            MultipartFile   certificatUrbanisme,
            MultipartFile   certificatPropriete,
            MultipartFile   devis,
            MultipartFile   planMasse,
            MultipartFile   planSituationTerrain,
            MultipartFile   cni,
            MultipartFile[] plansExecution) {

        log.info("PUT /dossier/update/{}", id);
        try {
            // 1. Récupération du dossier existant
            DossierPermisBatir dossier = this.dossierRepository.findById(id).orElse(null);
            if (dossier == null) {
                log.warn("updateDossierPermis → dossier introuvable, id={}", id);
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Dossier introuvable — ID : " + id, false));
            }

            // 2. Désérialisation
            DossierPermisDto dto = this.objectMapper.readValue(dossierJson, DossierPermisDto.class);

            // 3. Mise à jour des champs texte
            if (dto.getRaison() != null) dossier.setRaison(dto.getRaison());
            dossier.setDateModification(LocalDate.now());

            // 4. Remplacement des fichiers si de nouveaux sont fournis
            if (demandeTimbre != null && !demandeTimbre.isEmpty()) {
                supprimerFichierCloudinary(dossier.getDemandeTimbre());
                dossier.setDemandeTimbre(uploadFichier(demandeTimbre));
            }
            if (certificatUrbanisme != null && !certificatUrbanisme.isEmpty()) {
                supprimerFichierCloudinary(dossier.getCertificatUrbanisme());
                dossier.setCertificatUrbanisme(uploadFichier(certificatUrbanisme));
            }
            if (certificatPropriete != null && !certificatPropriete.isEmpty()) {
                supprimerFichierCloudinary(dossier.getCertificatPropriete());
                dossier.setCertificatPropriete(uploadFichier(certificatPropriete));
            }
            if (devis != null && !devis.isEmpty()) {
                supprimerFichierCloudinary(dossier.getDevis());
                dossier.setDevis(uploadFichier(devis));
            }
            if (planMasse != null && !planMasse.isEmpty()) {
                supprimerFichierCloudinary(dossier.getPlanMasse());
                dossier.setPlanMasse(uploadFichier(planMasse));
            }
            if (planSituationTerrain != null && !planSituationTerrain.isEmpty()) {
                supprimerFichierCloudinary(dossier.getPlanTerrain());
                dossier.setPlanTerrain(uploadFichier(planSituationTerrain));
            }
            if (cni != null && !cni.isEmpty()) {
                supprimerFichierCloudinary(dossier.getCni());
                dossier.setCni(uploadFichier(cni));
            }

            // 5. Sauvegarde du dossier mis à jour
            this.dossierRepository.save(dossier);
            log.info("updateDossierPermis → dossier mis à jour, numéro={}", dossier.getNumeroDossier());

            // 6. Ajout de nouveaux plans d'exécution si fournis
            if (plansExecution != null && plansExecution.length > 0) {
                List<Integer> typesIds = dto.getTypesPlansIds();
                if (typesIds == null || typesIds.size() != plansExecution.length) {
                    log.warn("updateDossierPermis → incohérence plans : {} types vs {} fichiers",
                            typesIds == null ? 0 : typesIds.size(), plansExecution.length);
                    return ResponseEntity.badRequest().body(new ServerReponse(
                            "Nombre de types de plans != nombre de fichiers plans", false));
                }

                List<PlanExecution> plans = new ArrayList<>();
                for (int i = 0; i < plansExecution.length; i++) {
                    if (plansExecution[i].isEmpty()) continue;

                    TypePlanExecution typePlan = this.typePlanExecutionRepository
                            .findById(typesIds.get(i)).orElse(null);
                    if (typePlan == null) {
                        log.warn("updateDossierPermis → TypePlanExecution introuvable, id={}", typesIds.get(i));
                        continue;
                    }

                    PlanExecution plan = new PlanExecution();
                    plan.setDossier(dossier);
                    plan.setTypePlan(typePlan);
                    plan.setChemin(uploadFichier(plansExecution[i]));
                    plans.add(plan);
                }
                this.planExecutionRepository.saveAll(plans);
            }

            return ResponseEntity.ok(new ServerReponse(
                    "MISE À JOUR DOSSIER PERMIS : SUCCESS — N° " + dossier.getNumeroDossier(), true));

        } catch (Exception e) {
            log.error("updateDossierPermis → erreur 500 inattendue, id={}", id, e);
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur interne du serveur : " + e.getMessage(), false));
        }
    }

    // ── deleteDossierPermis ───────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> deleteDossierPermis(Integer id) {
        log.info("DELETE /dossier/delete/{}", id);
        try {
            DossierPermisBatir dossier = this.dossierRepository.findById(id).orElse(null);
            if (dossier == null) {
                log.warn("deleteDossierPermis → dossier introuvable, id={}", id);
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Dossier introuvable — ID : " + id, false));
            }

            // 1. Suppression de tous les plans d'exécution sur Cloudinary + en base
            List<PlanExecution> plans = this.planExecutionRepository.findByDossier(dossier);
            for (PlanExecution plan : plans)
                supprimerFichierCloudinary(plan.getChemin());
            this.planExecutionRepository.deleteByDossier(dossier);

            // 2. Suppression des pièces justificatives sur Cloudinary
            supprimerFichierCloudinary(dossier.getDemandeTimbre());
            supprimerFichierCloudinary(dossier.getCertificatUrbanisme());
            supprimerFichierCloudinary(dossier.getCertificatPropriete());
            supprimerFichierCloudinary(dossier.getDevis());
            supprimerFichierCloudinary(dossier.getPlanMasse());
            supprimerFichierCloudinary(dossier.getPlanTerrain());
            supprimerFichierCloudinary(dossier.getCni());

            // 3. Suppression du dossier en base
            String numero = dossier.getNumeroDossier();
            this.dossierRepository.delete(dossier);

            log.info("deleteDossierPermis → dossier supprimé, numéro={}", numero);
            return ResponseEntity.ok(new ServerReponse(
                    "SUPPRESSION DOSSIER PERMIS : SUCCESS — N° " + numero, true));

        } catch (Exception e) {
            log.error("deleteDossierPermis → erreur 500 inattendue, id={}", id, e);
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur interne du serveur : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<List<PlanExecution>> getAllPlanExecutionByDossier(Integer id) {
        return ResponseEntity.ok(
                this.planExecutionRepository.findByDossier(
                        this.dossierRepository.findById(id).orElse(null)
                )
        );
    }

    @Override
    public ResponseEntity<List<TypePlanExecution>> getAlLPlanExecution() {
        return ResponseEntity.ok(this.typePlanExecutionRepository.findAll());
    }

    // ── Helpers privés ────────────────────────────────────────────────────

    private String uploadFichier(MultipartFile fichier) throws Exception {
        Map result = this.cloudinaryService.upload(fichier);
        return result.get("secure_url").toString();
    }

    private void supprimerFichierCloudinary(String secureUrl) {
        if (secureUrl == null || secureUrl.isBlank()) return;
        try {
            String[] parts    = secureUrl.split("/upload/");
            String   apres    = parts[1];
            String   sanVer   = apres.replaceFirst("v\\d+/", "");
            String   publicId = sanVer.substring(0, sanVer.lastIndexOf("."));
            this.cloudinaryService.delete(publicId);
        } catch (Exception e) {
            log.warn("supprimerFichierCloudinary → échec suppression, url={} | {}", secureUrl, e.getMessage());
        }
    }
}