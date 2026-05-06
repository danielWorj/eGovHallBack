package com.example.eHall.Entity.Domaine;

import com.example.eHall.Entity.Utilisateur.StatutUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class Etablissement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom ;
    private String telephone ;
    private String localisation ;
    private String email ;
    private Boolean actif ;
    private LocalDate creation;
    private String code ; //code la structure

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private TypeStructure type ;

    //1- ADMINISTRATION
    //2- HOPITAL
    //3- MAIRIE
}
