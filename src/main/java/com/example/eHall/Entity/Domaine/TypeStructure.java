package com.example.eHall.Entity.Domaine;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class TypeStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String nom ;
}
