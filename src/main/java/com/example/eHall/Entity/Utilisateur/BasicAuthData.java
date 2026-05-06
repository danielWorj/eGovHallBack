package com.example.eHall.Entity.Utilisateur;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
public class BasicAuthData {
    private Integer id;
    private Integer role;

    public BasicAuthData(Integer id, Integer role) {
        this.id = id;
        this.role = role;
    }
}
