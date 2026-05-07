package com.example.eHall.Controller.Utilisateur;

import com.example.eHall.Entity.Enfant.Sexe;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.BasicAuthData;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/eHall/api/user")
@CrossOrigin("*")
public interface UtilisateurControllerInt {

    //Sexe
    @GetMapping("/sexe/all")
    ResponseEntity<List<Sexe>> getAllSexe();
}
