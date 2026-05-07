package com.example.eHall.Controller.Declaration;

import com.example.eHall.Dto.Declaration.DeclarationDto;
import com.example.eHall.Entity.Acte.Declaration;
import com.example.eHall.Entity.Acte.PieceJointeDeclaration;
import com.example.eHall.Entity.Enfant.Enfant;
import com.example.eHall.Entity.Enfant.Sexe;
import com.example.eHall.Entity.Acte.TypePieceDeclaration;
import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.example.eHall.Repository.Acte.DeclarationRepository;
import com.example.eHall.Repository.Acte.PieceJointeDeclarationRepository;
import com.example.eHall.Repository.Acte.TypePieceJointeDeclarationRepository;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Enfant.EnfantRepository;
import com.example.eHall.Repository.Enfant.SexeRepository;
import com.example.eHall.Repository.Utilisateur.ParentRepository;
import com.example.eHall.Service.CloudinaryService;
import com.example.eHall.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class DeclarationControllerImpl implements DeclarationControllerInt {

    // Dossier de sauvegarde des fichiers (configurable dans application.properties)
    // Valeur par défaut : "templates/file/"
    @Value("${app.upload.dir:templates/file/}")
    private String uploadDir;

    @Autowired
    private DeclarationRepository declarationRepository;

    @Autowired
    private PieceJointeDeclarationRepository pieceJointeDeclarationRepository;

    @Autowired
    private TypePieceJointeDeclarationRepository typePieceJointeDeclarationRepository;

    @Autowired
    private StructureRepository structureRepository;

    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private EnfantRepository enfantRepository;
    @Autowired
    private SexeRepository sexeRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private EmailService emailService;

    @Override
    public ResponseEntity<List<Declaration>> findDeclaration() {
        return ResponseEntity.ok(this.declarationRepository.findAll());
    }

    @Override
    public ResponseEntity<List<Declaration>> findDeclarationByEtablissement(Integer id) {
        return ResponseEntity.ok(this.declarationRepository.findByStructure(
                this.structureRepository.findById(id).orElse(null)
        ));
    }

    @Override
    public ResponseEntity<ServerReponse> createDeclaration(String declaration, MultipartFile[] fichiers) {
        try {
            // 1. Désérialisation du DTO
            DeclarationDto declarationDto = this.objectMapper.readValue(declaration, DeclarationDto.class);

            // 2. Récupération de la structure
            Etablissement structure = this.structureRepository
                    .findById(declarationDto.getStructure())
                    .orElse(null);
            if (structure == null) {
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Structure introuvable", false));
            }

            // 3. Récupération du Sexe
            Sexe sexe = this.sexeRepository
                    .findById(declarationDto.getSexe())
                    .orElse(null);
            if (sexe == null) {
                return ResponseEntity.badRequest()
                        .body(new ServerReponse("Sexe introuvable", false));
            }

            // 4. Création de l'Enfant
            Enfant enfant = new Enfant();
            enfant.setNom(declarationDto.getNomEnfant());
            enfant.setPrenom(declarationDto.getPrenomEnfant());
            enfant.setSexe(sexe);
            enfant.setDateNaissance(LocalDate.parse(declarationDto.getDateNaissance()));
            enfant.setLieuNaissance(declarationDto.getLieuNaissance());

            Enfant enfantSaved = this.enfantRepository.save(enfant);

            // 5. Création de la Mère
            Parent mere = new Parent();
            mere.setNom(declarationDto.getNomParent());
            mere.setPrenom(declarationDto.getPrenomParent());
            mere.setTelephone(declarationDto.getTelephone());
            mere.setEmail(declarationDto.getEmail());
            mere.setStatus(false);
            mere.setCreation(LocalDate.now());
            mere.setModification(LocalDate.now());
            String password = "PDE" + declarationDto.getNomParent()
                    + declarationDto.getPrenomParent() + "XXX";
            mere.setPassword_hash(password);

            Parent mereSaved = this.parentRepository.save(mere);

            // 6. Création de la Déclaration
            Declaration declarationDB = new Declaration();
            declarationDB.setDate(LocalDate.now());
            declarationDB.setStructure(structure);
            declarationDB.setEnfant(enfantSaved);
            declarationDB.setMere(mereSaved);

            Declaration declarationSaved = this.declarationRepository.save(declarationDB);

            // 7. Traitement des pièces justificatives
            if (fichiers != null && fichiers.length > 0) {
                List<Integer> typeIds = declarationDto.getTypesPiecesJointes();

                if (typeIds == null || typeIds.size() != fichiers.length) {
                    return ResponseEntity.badRequest().body(
                            new ServerReponse(
                                    "Le nombre de types de pièces (" + (typeIds == null ? 0 : typeIds.size()) +
                                            ") ne correspond pas au nombre de fichiers (" + fichiers.length + ")",
                                    false
                            )
                    );
                }

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                List<PieceJointeDeclaration> piecesJointes = new ArrayList<>();

                for (int i = 0; i < fichiers.length; i++) {
                    MultipartFile fichier = fichiers[i];
                    Integer typeId = typeIds.get(i);

                    if (fichier.isEmpty()) continue;

                    TypePieceDeclaration typePiece = this.typePieceJointeDeclarationRepository
                            .findById(typeId)
                            .orElse(null);

                    if (typePiece == null) {
                        return ResponseEntity.badRequest().body(
                                new ServerReponse("Type de pièce introuvable pour l'ID : " + typeId, false)
                        );
                    }

//                    String extension = getExtension(fichier.getOriginalFilename());
//                    String nomFichierUnique = UUID.randomUUID().toString() + extension;
//                    Path cheminComplet = uploadPath.resolve(nomFichierUnique);
//                    fichier.transferTo(cheminComplet.toFile());

                    PieceJointeDeclaration piece = new PieceJointeDeclaration();


                    Map result = this.cloudinaryService.upload(fichier);
                    piece.setChemin(result.get("secure_url").toString());
                    System.out.println("media enregistre sur cloudinary avec url: "+ result.get("secure_url").toString());

                    piece.setDeclaration(declarationSaved);
                    piece.setType(typePiece);

                    piecesJointes.add(piece);
                }

                this.pieceJointeDeclarationRepository.saveAll(piecesJointes);
            }

            // 8. Envoi des emails
            this.emailService.sendSimpleEmail(
                    structure.getEmail(),
                    "Nouvelle déclaration de naissance",
                    "Bonjour " + structure.getNom() + ",\n\n" +
                            "Une nouvelle déclaration pour l'enfant " +
                            enfantSaved.getPrenom() + " " + enfantSaved.getNom() +
                            " a été créée avec succès.\n" +
                            "Nous vous reviendrons en cas de besoin.\n\n" +
                            "Cordialement,\nL'équipe eHall"
            );

            this.emailService.sendSimpleEmail(
                    mereSaved.getEmail(),
                    "Confirmation de votre déclaration",
                    "Bonjour " + mereSaved.getPrenom() + " " + mereSaved.getNom() + ",\n\n" +
                            "Votre déclaration pour l'enfant " +
                            enfantSaved.getPrenom() + " " + enfantSaved.getNom() +
                            " a été reçue avec succès dans nos services.\n" +
                            "De ce fait nous lançons immédiatement l'établissement de l'acte de naissance " +
                            "et la vérification de vos pièces justificatives.\n" +
                            "Si tout est en ordre, votre acte de naissance sera établi et vous serez informé pour le paiement des frais.\n" +
                            "En cas de besoin d'informations supplémentaires, nous vous contacterons rapidement.\n\n" +
                            "Pour suivre l'évolution du traitement, connectez-vous à votre espace personnel sur eHall.\n\n" +
                            "Cordialement,\nL'équipe eHall"
            );

            return ResponseEntity.ok(new ServerReponse("CREATION DECLARATION : SUCCESS", true));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    new ServerReponse("Erreur lors de la sauvegarde des fichiers : " + e.getMessage(), false)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ServerReponse("Erreur inattendue : " + e.getMessage(), false)
            );
        }
    }

    /**
     * Extrait l'extension d'un nom de fichier.
     * Retourne ".bin" si aucune extension n'est trouvée.
     */
    private String getExtension(String nomFichier) {
        if (nomFichier == null || !nomFichier.contains(".")) {
            return ".bin";
        }
        return nomFichier.substring(nomFichier.lastIndexOf("."));
    }
}