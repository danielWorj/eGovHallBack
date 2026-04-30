package com.example.eHall.Entity.Utilisateur;

import com.example.eHall.Entity.Domaine.Structure;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class Parent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String nom ;
    private String prenom ;
    private String telephone ;
    private String email ;
    private String password_hash ;
    private LocalDate creation ;
    private LocalDate modification ;
    private Boolean status ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private RoleUser roleUser ;

}
