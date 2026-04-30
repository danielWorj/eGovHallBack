package com.example.eHall.Dto.Declaration;

import lombok.Data;
import java.util.List;

@Data
public class DeclarationDto {
    private Integer id;
    private String nomEnfant;

    private String nomParent;
    private String prenomParent;
    private String telephone;
    private String email;
    private String localisation;

    private Integer structure;
    private Integer parent;

    // Liste des types de pièces jointes (IDs des TypePieceDeclaration)
    // Les fichiers sont transmis séparément via MultipartFile[]
    private List<Integer> typesPiecesJointes;
}
