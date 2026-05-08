package com.example.eHall.Entity.Utilisateur;

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
    private String profession ;
    private String domicile ;
    private LocalDate dateNaissance ;
    private String lieuNaissance ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private RoleUser roleUser ;

}
