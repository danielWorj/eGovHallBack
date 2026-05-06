package com.example.eHall.Controller.Utilisateur;

import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.BasicAuthData;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/eHall/api/auth")
@CrossOrigin("*")
public interface UtilisateurControllerInt {
    //
    @PostMapping("/create")
    ResponseEntity<ServerReponse> createUser(@RequestParam("user") String user);
    @PostMapping("/login")
    ResponseEntity<BasicAuthData> loginUser(@RequestParam("auth") String user);

}
