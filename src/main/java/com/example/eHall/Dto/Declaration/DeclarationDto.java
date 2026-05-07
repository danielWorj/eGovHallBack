package com.example.eHall.Dto.Declaration;

import lombok.Data;
import java.util.List;

@Data
public class DeclarationDto {
    private Integer id;

    // Les champs de l'enfant
    private String nomEnfant;
    private String prenomEnfant;
    private Integer sexe ;
    private String dateNaissance;
    private String lieuNaissance;

    // Les champs du parent
    private String nomParent;
    private String prenomParent;
    private String telephone;
    private String email;
    private String localisation;

    // Les champs de la structure
    private Integer structure;

    // Liste des types de pièces jointes (IDs des TypePieceDeclaration)
    // Les fichiers sont transmis séparément via MultipartFile[]
    private List<Integer> typesPiecesJointes;
}
