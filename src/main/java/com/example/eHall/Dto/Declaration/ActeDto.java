package com.example.eHall.Dto.Declaration;

import com.example.eHall.Entity.Acte.Declaration;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ActeDto {
    private Integer id ;
    private LocalDate date ;

    //Informatiosn sur la declaration
    private Integer declaration;

    //Informations sur le pere
    private String nomPere ;
    private String prenomPere ;
    private String telephonePere ;
    private String emailPere ;
    private String profession ;
    private String domicile ;
    private String dateNaissance ;
    private String lieuNaissance ;

    // Liste des types de pièces jointes (IDs des TypePieceDeclaration)
    // Les fichiers sont transmis séparément via MultipartFile[]
    private List<Integer> typesPiecesJointes;
}
