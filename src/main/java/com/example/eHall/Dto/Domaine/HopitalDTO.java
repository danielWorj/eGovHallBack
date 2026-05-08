package com.example.eHall.Dto.Domaine;

import lombok.Data;

@Data
public class HopitalDTO {
    private Integer id;
    private String  nom;
    private String  telephone;
    private String  localisation;
    private String  email;
    // ID de la mairie à laquelle cet hôpital est rattaché
    private Integer mairieId;
}
