package com.example.eHall.Entity.Acte;

import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Domaine.Hopital;
import com.example.eHall.Entity.Domaine.Mairie;
import com.example.eHall.Entity.Domaine.Structure;
import com.example.eHall.Entity.Enfant.Enfant;
import com.example.eHall.Entity.PieceJustificative.PieceJustificative;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table
@Data
public class Declaration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate date ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Hopital hopital ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Mairie mairie ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Enfant enfant ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Parent mere ;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<PieceJustificative> pieceJustificatives;

}
