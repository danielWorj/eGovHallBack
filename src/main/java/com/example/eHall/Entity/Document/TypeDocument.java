package com.example.eHall.Entity.Document;

import com.example.eHall.Entity.Domaine.TypeStructure;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class TypeDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id ;
    private String titre ;
    private LocalDate created ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private TypeStructure type ;

}
