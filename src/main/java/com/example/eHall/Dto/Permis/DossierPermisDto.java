package com.example.eHall.Dto.Permis;

import lombok.Data;

import java.util.List;

@Data
public class DossierPermisDto {

    // Infos du demandeur — recherché en base, sinon créé
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String raison ;

    // Infos complémentaires demandeur
    private String raisonSociale; // si personne morale

    // Mairie destinataire
    private Integer mairieId;



    // IDs des types de plans d'exécution (TypePlanExecution)
    // L'ordre doit correspondre à l'ordre des fichiers dans le multipart "plansExecution"
    // ex: [1, 2, 3] pour FONDATIONS, TOITURE, FACADES
    private List<Integer> typesPlansIds;
}