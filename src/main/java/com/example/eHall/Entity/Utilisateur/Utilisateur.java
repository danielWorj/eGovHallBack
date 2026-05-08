package com.example.eHall.Entity.Utilisateur;

import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Domaine.Structure;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String nom ;
    private String prenom ;
    private String telephone ;
    private String email ;
    private String password ;
    private LocalDate creation ;
    private LocalDate modification ;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private StatutUser statutUser ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private RoleUser roleUser ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Structure structure ; //Maire ou hopital



}


