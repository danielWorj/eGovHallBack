package com.example.eHall.Controller.Utilisateur;

import com.example.eHall.Dto.Utilisateur.LoginDTO;
import com.example.eHall.Dto.Utilisateur.UtilisateurDto;
import com.example.eHall.Entity.Enfant.Sexe;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.BasicAuthData;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Enfant.SexeRepository;
import com.example.eHall.Repository.Utilisateur.RoleUserRepository;
import com.example.eHall.Repository.Utilisateur.StatutUserRepository;
import com.example.eHall.Repository.Utilisateur.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
public class UtilisateurControllerImpl implements UtilisateurControllerInt {
    @Autowired
    private SexeRepository sexeRepository;

    @Override
    public ResponseEntity<List<Sexe>> getAllSexe() {
        return ResponseEntity.ok(this.sexeRepository.findAll());
    }
}
