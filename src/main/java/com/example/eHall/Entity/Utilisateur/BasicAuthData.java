package com.example.eHall.Entity.Utilisateur;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
public class BasicAuthData {
    private Integer id;
    private Integer role;
    private Integer etablissement;

    public BasicAuthData(Integer id, Integer role, Integer etablissement) {
        this.id = id;
        this.role = role;
        this.etablissement = etablissement;
    }


}
