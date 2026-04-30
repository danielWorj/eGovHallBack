package com.example.eHall.Entity.PieceJustificative;

import com.example.eHall.Entity.Utilisateur.StatutUser;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Parent;

@Entity
@Data
@Table
public class PieceJustificative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String chemin ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private TypePiece type ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Utilisateur utilisateur ;


}
