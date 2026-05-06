package com.example.eHall.Entity.Acte;

import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Domaine.TypeStructure;
import com.example.eHall.Entity.PieceJustificative.PieceJustificative;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table
@Data
public class Declaration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nomEnfant ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Etablissement structure ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Parent parent ;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<PieceJustificative> pieceJustificatives;




}
