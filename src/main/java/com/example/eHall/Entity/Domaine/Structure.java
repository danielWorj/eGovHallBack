package com.example.eHall.Entity.Domaine;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED) //Heritage
@DiscriminatorColumn(name = "type_structure")
public class Structure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom ;
    private String telephone ;
    private String localisation ;
    private String email ;
    private Boolean actif ;
    private LocalDate creation;
    private String code ;
}
