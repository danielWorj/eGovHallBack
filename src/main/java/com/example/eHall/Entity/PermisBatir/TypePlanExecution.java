package com.example.eHall.Entity.PermisBatir;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class TypePlanExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String intitule ;
    //1-
}
