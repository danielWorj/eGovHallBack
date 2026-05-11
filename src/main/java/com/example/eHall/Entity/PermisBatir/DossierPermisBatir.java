package com.example.eHall.Entity.PermisBatir;

import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table
@Data
public class DossierPermisBatir {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String numeroDossier ;
    private LocalDate date;
    private LocalDate dateInstruction;
    private LocalDate dateModification;

    //Demamdeur
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Utilisateur demandeur ;
    private String cni;//url secure  du fichier
    @Lob
    private String raison; //Raison de la demande du permis
    // Structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Mairie mairie ; //A quel mairie est adresse la demande

    //URL des fichiers
    private String demandeTimbre ;//url secure  du fichier
    private String certificatUrbanisme;//url secure  du fichier
    private String certificatPropriete;//url secure  du fichier
    private String devis ;//url secure  du fichier
    private String planTerrain ;//url secure  du fichier
    private String planMasse ;//url secure  du fichier

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private StatutDossier statut ;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PlanExecution> planExecutions ;

}
