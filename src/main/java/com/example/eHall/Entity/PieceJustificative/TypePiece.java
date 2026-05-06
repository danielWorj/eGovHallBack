package com.example.eHall.Entity.PieceJustificative;

import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Utilisateur.StatutUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class TypePiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Etablissement structure ;


}
