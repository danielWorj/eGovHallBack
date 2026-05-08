package com.example.eHall.Entity.Domaine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
@DiscriminatorValue(value = "mairie")

public class Hopital extends Structure {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Mairie mairie ;
}