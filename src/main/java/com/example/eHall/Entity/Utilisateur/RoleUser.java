package com.example.eHall.Entity.Utilisateur;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class RoleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String nom ;
    @Lob
    private String description ;

    //1- ADMIN
    //2- AGENT HOPITAL
    //3- AGENT MAIRIE
    //4- PARENT
}
