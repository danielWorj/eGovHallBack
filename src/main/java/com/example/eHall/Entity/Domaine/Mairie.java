package com.example.eHall.Entity.Domaine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Entity
@Table
@Data
@DiscriminatorValue(value = "mairie")

public class Mairie extends Structure {

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    private List<Hopital> hopitaux;
}
