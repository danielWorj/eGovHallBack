package com.example.eHall.Controller.Utilisateur;

import com.example.eHall.Entity.Server.ServerReponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/user")
@CrossOrigin("*")
public interface UtilisateurControllerInt {
    //
    @PostMapping("/create")
    ResponseEntity<ServerReponse> createUser(@Param("user") String user);

}
