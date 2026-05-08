package com.example.eHall.Controller.Declaration;

import com.example.eHall.Dto.Declaration.ActeDto;
import com.example.eHall.Dto.Declaration.DeclarationDto;
import com.example.eHall.Entity.Acte.*;
import com.example.eHall.Entity.Domaine.Hopital;
import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.Enfant.Enfant;
import com.example.eHall.Entity.Enfant.Sexe;
import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.example.eHall.Repository.Acte.*;
import com.example.eHall.Repository.Domaine.HopitalRepository;
import com.example.eHall.Repository.Domaine.MairieRepository;
import com.example.eHall.Repository.Domaine.StructureHRepository;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Enfant.EnfantRepository;
import com.example.eHall.Repository.Enfant.SexeRepository;
import com.example.eHall.Repository.Utilisateur.ParentRepository;
import com.example.eHall.Repository.Utilisateur.RoleUserRepository;
import com.example.eHall.Repository.Utilisateur.StatutUserRepository;
import com.example.eHall.Service.ActeService;
import com.example.eHall.Service.CloudinaryService;
import com.example.eHall.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Paths;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class DeclarationControllerImpl implements DeclarationControllerInt {

    @Value("${app.upload.dir:templates/file/}")
    private String uploadDir;

    @Autowired private DeclarationRepository           declarationRepository;
    @Autowired private ActeNaissanceRepository         acteNaissanceRepository;
    @Autowired private PieceJointeDeclarationRepository pieceJointeDeclarationRepository;
    @Autowired private TypePieceJointeDeclarationRepository typePieceJointeDeclarationRepository;
    @Autowired private PieceJointeActeRepository       pieceJointeActeRepository;
    @Autowired private StructureRepository             structureRepository;
    @Autowired private StructureHRepository            structureHRepository;
    @Autowired private MairieRepository                mairieRepository;
    @Autowired private HopitalRepository               hopitalRepository;
    @Autowired private ParentRepository                parentRepository;
    @Autowired private EnfantRepository                enfantRepository;
    @Autowired private SexeRepository                  sexeRepository;
    @Autowired private RoleUserRepository roleUserRepository;
    @Autowired private StatutUserRepository statutUserRepository;
    @Autowired private CloudinaryService               cloudinaryService;
    @Autowired private EmailService                    emailService;
    @Autowired private ActeService acteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── findDeclaration ───────────────────────────────────────────────────
    @Override
    public ResponseEntity<List<Declaration>> findDeclaration() {
        return ResponseEntity.ok(this.declarationRepository.findAll());
    }

    @Override
    public ResponseEntity<List<Declaration>> findDeclarationByHopital(Integer id) {
        return ResponseEntity.ok(this.declarationRepository.findByHopital(
                this.hopitalRepository.findById(id).orElse(null)));
    }

    @Override
    public ResponseEntity<List<Declaration>> findDeclarationByMairie(Integer id) {
        return ResponseEntity.ok(this.declarationRepository.findByMairie(
                this.mairieRepository.findById(id).orElse(null)));
    }

    // ── createDeclaration ─────────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> createDeclaration(String declaration, MultipartFile[] fichiers) {
        try {
            DeclarationDto declarationDto = this.objectMapper.readValue(declaration, DeclarationDto.class);

            Hopital hopital = this.hopitalRepository.findById(declarationDto.getHopital()).orElse(null);
            Mairie  mairie  = this.mairieRepository.findById(declarationDto.getMairie()).orElse(null);

            if (hopital == null)
                return ResponseEntity.badRequest().body(new ServerReponse("Hopital introuvable", false));
            if (mairie == null)
                return ResponseEntity.badRequest().body(new ServerReponse("Mairie introuvable", false));

            Sexe sexe = this.sexeRepository.findById(declarationDto.getSexe()).orElse(null);
            if (sexe == null)
                return ResponseEntity.badRequest().body(new ServerReponse("Sexe introuvable", false));

            Enfant enfant = new Enfant();
            enfant.setNom(declarationDto.getNomEnfant());
            enfant.setPrenom(declarationDto.getPrenomEnfant());
            enfant.setSexe(sexe);
            enfant.setDateNaissance(LocalDate.parse(declarationDto.getDateNaissance()));
            enfant.setLieuNaissance(declarationDto.getLieuNaissance());
            Enfant enfantSaved = this.enfantRepository.save(enfant);

            Parent mere = new Parent();
            mere.setNom(declarationDto.getNomParent());
            mere.setPrenom(declarationDto.getPrenomParent());
            mere.setTelephone(declarationDto.getTelephone());
            mere.setEmail(declarationDto.getEmail());
            mere.setStatus(false);
            mere.setCreation(LocalDate.now());
            mere.setProfession(declarationDto.getProfession());
            mere.setDateNaissance(LocalDate.parse(declarationDto.getDateNaissanceM()));
            mere.setLieuNaissance(declarationDto.getLieuNaissanceM());
            mere.setDomicile(declarationDto.getLocalisation());
            mere.setModification(LocalDate.now());
            mere.setRoleUser(this.roleUserRepository.findById(4).orElse(null));
            mere.setPassword_hash("PDE" + declarationDto.getNomParent() + declarationDto.getPrenomParent() + "XXX");
            Parent mereSaved = this.parentRepository.save(mere);

            Declaration declarationDB = new Declaration();
            declarationDB.setDate(LocalDate.now());
            declarationDB.setHopital(hopital);
            declarationDB.setMairie(mairie);
            declarationDB.setEnfant(enfantSaved);
            declarationDB.setMere(mereSaved);
            Declaration declarationSaved = this.declarationRepository.save(declarationDB);

            if (fichiers != null && fichiers.length > 0) {
                List<Integer> typeIds = declarationDto.getTypesPiecesJointes();
                if (typeIds == null || typeIds.size() != fichiers.length)
                    return ResponseEntity.badRequest().body(new ServerReponse(
                            "Nombre de types != nombre de fichiers", false));

                List<PieceJointeDeclaration> pieces = new ArrayList<>();
                for (int i = 0; i < fichiers.length; i++) {
                    if (fichiers[i].isEmpty()) continue;
                    TypePieceDeclaration type = this.typePieceJointeDeclarationRepository
                            .findById(typeIds.get(i)).orElse(null);
                    if (type == null)
                        return ResponseEntity.badRequest().body(
                                new ServerReponse("Type introuvable ID : " + typeIds.get(i), false));

                    Map result = this.cloudinaryService.upload(fichiers[i]);
                    PieceJointeDeclaration p = new PieceJointeDeclaration();
                    p.setDeclaration(declarationSaved);
                    p.setType(type);
                    p.setChemin(result.get("secure_url").toString());
                    pieces.add(p);
                }
                this.pieceJointeDeclarationRepository.saveAll(pieces);
            }

            this.emailService.sendSimpleEmail(
                    mereSaved.getEmail(),
                    "Déclaration de naissance reçue — eHall",
                    "Bonjour " + mereSaved.getPrenom() + " " + mereSaved.getNom() + ",\n\n" +
                            "Votre déclaration pour l'enfant " + enfantSaved.getPrenom() + " " + enfantSaved.getNom() +
                            " a été reçue avec succès.\n\nCordialement,\nL'équipe eHall"
            );

            return ResponseEntity.ok(new ServerReponse("CREATION DECLARATION : SUCCESS", true));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur fichiers : " + e.getMessage(), false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur inattendue : " + e.getMessage(), false));
        }
    }

    // ── findAllActeNaissanceByMairie ──────────────────────────────────────
    @Override
    public ResponseEntity<List<ActeNaissance>> findAllActeNaissanceByMairie(Integer id) {
        return ResponseEntity.ok(this.acteNaissanceRepository.findAllActeByMairie(id));
    }

    // ── creationActeNaissance ─────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> creationActeNaissance(String acte, MultipartFile[] fichiers) {
        try {
            ActeDto acteDto = this.objectMapper.readValue(acte, ActeDto.class);

            Declaration declaration = this.declarationRepository
                    .findById(acteDto.getDeclaration()).orElse(null);
            if (declaration == null)
                return ResponseEntity.badRequest().body(
                        new ServerReponse("Déclaration introuvable ID : " + acteDto.getDeclaration(), false));

            Parent pere = new Parent();
            pere.setNom(acteDto.getNomPere());
            pere.setPrenom(acteDto.getPrenomPere());
            pere.setTelephone(acteDto.getTelephonePere());
            pere.setEmail(acteDto.getEmailPere());
            pere.setStatus(false);
            pere.setCreation(LocalDate.now());
            pere.setModification(LocalDate.now());
            pere.setDateNaissance(LocalDate.parse(acteDto.getDateNaissance()));
            pere.setLieuNaissance(acteDto.getLieuNaissance());
            pere.setDomicile(acteDto.getDomicile());
            pere.setProfession(acteDto.getProfession());
            pere.setPassword_hash("PDE" + acteDto.getNomPere() + acteDto.getPrenomPere() + "XXX");
            Parent pereSaved = this.parentRepository.save(pere);

            String numeroActe = LocalDate.now().getYear()
                    + "-" + declaration.getMairie().getId()
                    + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            ActeNaissance acteNaissance = new ActeNaissance();
            acteNaissance.setNumeroActe(numeroActe);
            acteNaissance.setDate(LocalDate.now());
            acteNaissance.setDeclaration(declaration);
            acteNaissance.setPere(pereSaved);
            ActeNaissance acteNaissanceCreated = this.acteNaissanceRepository.save(acteNaissance);

            if (fichiers != null && fichiers.length > 0) {
                List<Integer> typeIds = acteDto.getTypesPiecesJointes();
                if (typeIds == null || typeIds.size() != fichiers.length)
                    return ResponseEntity.badRequest().body(new ServerReponse(
                            "Nombre de types (" + (typeIds == null ? 0 : typeIds.size()) +
                                    ") != nombre de fichiers (" + fichiers.length + ")", false));

                List<PieceJointeActeNaissance> pieces = new ArrayList<>();
                for (int i = 0; i < fichiers.length; i++) {
                    if (fichiers[i].isEmpty()) continue;
                    TypePieceDeclaration type = this.typePieceJointeDeclarationRepository
                            .findById(typeIds.get(i)).orElse(null);
                    if (type == null)
                        return ResponseEntity.badRequest().body(
                                new ServerReponse("Type introuvable ID : " + typeIds.get(i), false));

                    Map result = this.cloudinaryService.upload(fichiers[i]);
                    PieceJointeActeNaissance p = new PieceJointeActeNaissance();
                    p.setActeNaissance(acteNaissanceCreated);
                    p.setType(type);
                    p.setChemin(result.get("secure_url").toString());
                    pieces.add(p);
                }
                this.pieceJointeActeRepository.saveAll(pieces);
            }

            LocalDate dateRdv    = LocalDate.now().plusDays(10);
            String rdvFormate    = dateRdv.getDayOfMonth() + "/" + dateRdv.getMonthValue() + "/" + dateRdv.getYear();
            String prenomEnfant  = declaration.getEnfant().getPrenom();
            String nomEnfant     = declaration.getEnfant().getNom();
            String localisation  = declaration.getMairie().getLocalisation() != null
                    ? declaration.getMairie().getLocalisation() : declaration.getMairie().getNom();
            Parent mere          = declaration.getMere();

            String corpsEmail =
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "  Numéro de l'acte     : " + numeroActe + "\n" +
                            "  Enfant               : " + prenomEnfant + " " + nomEnfant + "\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "📅 RENDEZ-VOUS : le " + rdvFormate + "\n" +
                            "📍 Lieu        : " + localisation + "\n\n" +
                            "Merci de vous présenter muni(e) d'une pièce d'identité valide.\n\n" +
                            "Cordialement,\nL'équipe eHall";

            this.emailService.sendSimpleEmail(mere.getEmail(),
                    "✅ Acte de naissance établi — Rendez-vous le " + rdvFormate,
                    "Bonjour " + mere.getPrenom() + " " + mere.getNom() + ",\n\n" +
                            "L'acte de naissance de votre enfant a été établi.\n\n" + corpsEmail);

            this.emailService.sendSimpleEmail(pereSaved.getEmail(),
                    "✅ Acte de naissance établi — Rendez-vous le " + rdvFormate,
                    "Bonjour " + pereSaved.getPrenom() + " " + pereSaved.getNom() + ",\n\n" +
                            "L'acte de naissance de votre enfant a été établi.\n\n" + corpsEmail);

            return ResponseEntity.ok(new ServerReponse(
                    "CREATION ACTE NAISSANCE : SUCCESS — N° " + numeroActe, true));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur fichiers : " + e.getMessage(), false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur inattendue : " + e.getMessage(), false));
        }
    }

    // ── updateActeNaissance ───────────────────────────────────────────────
    @Override
    public ResponseEntity<ServerReponse> updateActeNaissance(
            Integer id, String acte, MultipartFile[] fichiers) {
        try {
            // 1. Désérialisation
            ActeDto acteDto = this.objectMapper.readValue(acte, ActeDto.class);

            // 2. Récupération de l'acte existant
            ActeNaissance acteNaissance = this.acteNaissanceRepository.findById(id).orElse(null);
            if (acteNaissance == null)
                return ResponseEntity.badRequest().body(
                        new ServerReponse("Acte introuvable — ID : " + id, false));

            // 3. Mise à jour du père
            Parent pere = acteNaissance.getPere();
            if (pere == null) {
                // Cas exceptionnel : père non rattaché, on en crée un nouveau
                pere = new Parent();
                pere.setCreation(LocalDate.now());
                pere.setStatus(false);
                pere.setPassword_hash("PDE" + acteDto.getNomPere() + acteDto.getPrenomPere() + "XXX");
            }
            pere.setNom(acteDto.getNomPere());
            pere.setPrenom(acteDto.getPrenomPere());
            pere.setTelephone(acteDto.getTelephonePere());
            pere.setEmail(acteDto.getEmailPere());
            pere.setModification(LocalDate.now());
            this.parentRepository.save(pere);

            // 4. Mise à jour de la date de l'acte
            if (acteDto.getDate() != null) {
                acteNaissance.setDate(acteDto.getDate());
            }
            this.acteNaissanceRepository.save(acteNaissance);

            // 5. Pièces jointes supplémentaires (optionnel)
            if (fichiers != null && fichiers.length > 0) {
                List<Integer> typeIds = acteDto.getTypesPiecesJointes();
                if (typeIds != null && typeIds.size() == fichiers.length) {
                    Declaration declaration = acteNaissance.getDeclaration();
                    List<PieceJointeActeNaissance> pieces = new ArrayList<>();
                    for (int i = 0; i < fichiers.length; i++) {
                        if (fichiers[i].isEmpty()) continue;
                        TypePieceDeclaration type = this.typePieceJointeDeclarationRepository
                                .findById(typeIds.get(i)).orElse(null);
                        if (type == null) continue;

                        Map result = this.cloudinaryService.upload(fichiers[i]);
                        PieceJointeActeNaissance p = new PieceJointeActeNaissance();
                        p.setActeNaissance(acteNaissance);
                        p.setType(type);
                        p.setChemin(result.get("secure_url").toString());
                        pieces.add(p);
                    }
                    this.pieceJointeActeRepository.saveAll(pieces);
                }
            }

            return ResponseEntity.ok(
                    new ServerReponse("MISE A JOUR ACTE : SUCCESS — N° " + acteNaissance.getNumeroActe(), true));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur fichiers : " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ServerReponse("Erreur inattendue : " + e.getMessage(), false));
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadActeNaissance(Integer id) {
        try {
            ActeNaissance acte = this.acteNaissanceRepository.findById(id).orElse(null);
            if (acte == null)
                return ResponseEntity.notFound().build();

            String cheminPdf = this.acteService.generer(acte);

            byte[] pdfBytes = Files.readAllBytes(Paths.get(cheminPdf));

            String nomFichier = "acte_naissance_" + acte.getNumeroActe() + ".pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", nomFichier);
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── private helper ────────────────────────────────────────────────────
    private String getExtension(String nomFichier) {
        if (nomFichier == null || !nomFichier.contains(".")) return ".bin";
        return nomFichier.substring(nomFichier.lastIndexOf("."));
    }
}