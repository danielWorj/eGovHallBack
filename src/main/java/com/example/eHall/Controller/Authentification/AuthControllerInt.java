package com.example.eHall.Controller.Authentification;

import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.BasicAuthData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/eHall/api/auth")
@CrossOrigin("*")
public interface AuthControllerInt {
    @PostMapping("/create")
    ResponseEntity<ServerReponse> createUser(@RequestParam("user") String user);
    @PostMapping("/login")
    ResponseEntity<BasicAuthData> loginUser(@RequestParam("auth") String user);

}
