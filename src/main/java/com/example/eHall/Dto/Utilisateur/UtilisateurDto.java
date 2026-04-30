package com.example.eHall.Dto.Utilisateur;

import com.example.eHall.Entity.Domaine.Structure;
import com.example.eHall.Entity.Utilisateur.RoleUser;
import com.example.eHall.Entity.Utilisateur.StatutUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UtilisateurDto {
    private Integer id ;
    private String nom ;
    private String prenom ;
    private String telephone ;
    private String email ;
    private String password_hash ;
    private LocalDate creation ;
    private LocalDate modification ;
    private Integer statutUser ;
    private Integer roleUser ;
    private Integer structure ;
}
