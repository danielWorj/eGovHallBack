package com.example.eHall.Controller.Declaration;

import com.example.eHall.Dto.Declaration.DeclarationDto;
import com.example.eHall.Entity.Acte.Declaration;
import com.example.eHall.Entity.Acte.PieceJointeDeclaration;
import com.example.eHall.Entity.Acte.TypePieceDeclaration;
import com.example.eHall.Entity.Domaine.Structure;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.example.eHall.Repository.Acte.DeclarationRepository;
import com.example.eHall.Repository.Acte.PieceJointeDeclarationRepository;
import com.example.eHall.Repository.Acte.TypePieceJointeDeclarationRepository;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Utilisateur.ParentRepository;
import com.example.eHall.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private EmailService emailService;

    @Override
    public ResponseEntity<ServerReponse> createDeclaration(String declaration, MultipartFile[] fichiers) {
        try {
            // 1. Désérialisation du DTO
            DeclarationDto declarationDto = this.objectMapper.readValue(declaration, DeclarationDto.class);
            // Recuperation d ela structure
            Structure structure = this.structureRepository.findById(declarationDto.getStructure()).orElse(null);
            // 2. Création du Parent
            Parent parentDB = new Parent();
            parentDB.setNom(declarationDto.getNomParent());
            parentDB.setPrenom(declarationDto.getPrenomParent());
            parentDB.setTelephone(declarationDto.getTelephone());
            parentDB.setEmail(declarationDto.getEmail());
            parentDB.setStatus(false);
            parentDB.setCreation(LocalDate.now());
            parentDB.setModification(LocalDate.now());
            String password = "PDE" + declarationDto.getNomParent() + declarationDto.getPrenomParent() + "XXX";
            parentDB.setPassword_hash(password);

            Parent parentCreated = this.parentRepository.save(parentDB);

            // 3. Création de la Déclaration
            Declaration declarationDB = new Declaration();
            declarationDB.setNomEnfant(declarationDto.getNomEnfant());
            declarationDB.setParent(parentCreated);
            declarationDB.setStructure(structure);

            Declaration declarationSaved = this.declarationRepository.save(declarationDB);

            // 4. Traitement des pièces justificatives
            if (fichiers != null && fichiers.length > 0) {
                List<Integer> typeIds = declarationDto.getTypesPiecesJointes();

                // Vérification cohérence nombre de fichiers / nombre de types
                if (typeIds == null || typeIds.size() != fichiers.length) {
                    return ResponseEntity.badRequest().body(
                            new ServerReponse(
                                    "Le nombre de types de pièces (" + (typeIds == null ? 0 : typeIds.size()) +
                                            ") ne correspond pas au nombre de fichiers (" + fichiers.length + ")",
                                    false
                            )
                    );
                }

                // Création du dossier de stockage si inexistant
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                List<PieceJointeDeclaration> piecesJointes = new ArrayList<>();

                for (int i = 0; i < fichiers.length; i++) {
                    MultipartFile fichier = fichiers[i];
                    Integer typeId = typeIds.get(i);

                    if (fichier.isEmpty()) {
                        continue; // On ignore les fichiers vides
                    }

                    // Récupération du type de pièce
                    TypePieceDeclaration typePiece = this.typePieceJointeDeclarationRepository
                            .findById(typeId)
                            .orElse(null);

                    if (typePiece == null) {
                        return ResponseEntity.badRequest().body(
                                new ServerReponse("Type de pièce introuvable pour l'ID : " + typeId, false)
                        );
                    }

                    // Génération d'un nom de fichier unique pour éviter les collisions
                    String extension = getExtension(fichier.getOriginalFilename());
                    String nomFichierUnique = UUID.randomUUID().toString() + extension;
                    Path cheminComplet = uploadPath.resolve(nomFichierUnique);

                    // Sauvegarde physique du fichier
                    fichier.transferTo(cheminComplet.toFile());

                    // Création de l'entité PieceJointeDeclaration
                    PieceJointeDeclaration piece = new PieceJointeDeclaration();
                    piece.setDeclaration(declarationSaved);
                    piece.setType(typePiece);
                    piece.setChemin(nomFichierUnique);

                    piecesJointes.add(piece);
                }

                // Sauvegarde en base de toutes les pièces jointes
                this.pieceJointeDeclarationRepository.saveAll(piecesJointes);

                //Envoi du mail a la structure: Hopital
                this.emailService.sendSimpleEmail(
                        structure.getEmail(),
                        "Confirmation de votre déclaration",
                        "Bonjour " + structure.getNom() + ",\n\n" +
                                "Votre déclaration pour l'enfant " + declarationSaved.getNomEnfant() + " a été créée avec succès.\n" +
                                "Nous vous reviendrons en cas de besoin.\n\n" +
                                "Cordialement,\n" +
                                "L'équipe eHall"
                );

                //Envoie du mail au parent
                this.emailService.sendSimpleEmail(
                        parentCreated.getEmail(),
                        "Confirmation de votre déclaration",
                        "Bonjour " + parentCreated.getPrenom() + " " + parentCreated.getNom() + ",\n\n" +
                                "Votre déclaration pour l'enfant " + declarationSaved.getNomEnfant() + " a été recu avec succès dans nos services.\n" +
                                "De ce fait nous lancons immediatement l'etablissement de l'acte de naissance et la verification de vos pieces justificatives.\n" +
                                "Si tout est en ordre, votre acte de naissance sera établi et vous serez informé pour le paiement des frais.\n" +
                                "En cas de besoin d'informations supplémentaires ou de documents complémentaires, nous vous contacterons rapidement.\n\n" +
                                "Pour suivre l'evolution des traitements de votre déclaration, vous pouvez vous connecter à votre espace personnel sur notre plateforme eHall.\n" +
                                "Cordialement,\n" +
                                "L'équipe eHall"
                );
            }

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