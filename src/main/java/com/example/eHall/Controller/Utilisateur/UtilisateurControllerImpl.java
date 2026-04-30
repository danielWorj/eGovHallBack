package com.example.eHall.Controller.Utilisateur;

import com.example.eHall.Dto.Utilisateur.UtilisateurDto;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Entity.Utilisateur.Utilisateur;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Utilisateur.RoleUserRepository;
import com.example.eHall.Repository.Utilisateur.StatutUserRepository;
import com.example.eHall.Repository.Utilisateur.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Controller
public class UtilisateurControllerImpl implements UtilisateurControllerInt {
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private StructureRepository structureRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;
    @Autowired
    private StatutUserRepository statutUserRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<ServerReponse> createUser(String user) {
        UtilisateurDto utilisateurDto = this.objectMapper.readValue(user, UtilisateurDto.class);

        Utilisateur utilisateurDB = new Utilisateur();

        utilisateurDB.setNom(utilisateurDto.getNom());
        utilisateurDB.setPrenom(utilisateurDto.getPrenom());
        utilisateurDB.setEmail(utilisateurDto.getEmail());
        utilisateurDB.setModification(LocalDate.now());
        utilisateurDB.setCreation(LocalDate.now());

        utilisateurDB.setRoleUser(this.roleUserRepository.findById(utilisateurDto.getRoleUser()).orElse(null));
        utilisateurDB.setStatutUser(this.statutUserRepository.findById(utilisateurDto.getStatutUser()).orElse(null));

        utilisateurDB.setPassword_hash(utilisateurDto.getPassword_hash());

        return ResponseEntity.ok(new ServerReponse("CREATION USER : success", true));
    }


}
