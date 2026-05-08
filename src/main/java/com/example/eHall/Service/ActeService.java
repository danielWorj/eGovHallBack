package com.example.eHall.Service;

import com.example.eHall.Entity.Acte.ActeNaissance;
import com.example.eHall.Utils.ActeGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ActeService {


    public String generer(ActeNaissance acteNaissance) {
        if (acteNaissance == null) {
            throw new IllegalArgumentException("L'acte de naissance ne peut pas être null.");
        }

        try {
            ActeGenerator generator = new ActeGenerator(acteNaissance);
            String cheminPdf = generator.generer();
            return cheminPdf;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la génération du PDF pour l'acte n° "
                            + acteNaissance.getNumeroActe(), e);
        }
    }
}