package com.example.eHall.Controller.Domaine;

import com.example.eHall.Dto.Domaine.StructureDto;
import com.example.eHall.Entity.Domaine.Etablissement;
import com.example.eHall.Entity.Server.ServerReponse;
import com.example.eHall.Repository.Domaine.StructureRepository;
import com.example.eHall.Repository.Domaine.TypeStructureRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DomaineControllerImpl implements DomaineControllerInt{
    @Autowired
    private StructureRepository structureRepository;
    @Autowired
    private TypeStructureRepository typeStructureRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<List<Etablissement>> findAllStructure() {
        return ResponseEntity.ok(this.structureRepository.findAll());
    }

    @Override
    public ResponseEntity<ServerReponse> createStructure(String structure) {
        StructureDto structureDto = this.objectMapper.readValue(structure, StructureDto.class);

        Etablissement structureDB = new Etablissement();

        structureDB.setNom(structureDto.getNom());
        structureDB.setActif(false);
        structureDB.setEmail(structureDto.getEmail());
        structureDB.setLocalisation(structureDto.getLocalisation());
        structureDB.setTelephone(structureDto.getTelephone());
        structureDB.setCreation(LocalDate.now());
        structureDB.setType(this.typeStructureRepository.findById(structureDto.getId()).orElse(null));

        this.structureRepository.save(structureDB);

        return ResponseEntity.ok(new ServerReponse("Structure cree",true));
    }

    @Override
    public ResponseEntity<ServerReponse> updateStructure(String structure) {
        StructureDto structureDto = this.objectMapper.readValue(structure, StructureDto.class);

        Etablissement structureDB = new Etablissement();

        structureDB.setId(structureDto.getId());
        structureDB.setNom(structureDto.getNom());
        structureDB.setActif(false);
        structureDB.setEmail(structureDto.getEmail());
        structureDB.setLocalisation(structureDto.getLocalisation());
        structureDB.setTelephone(structureDto.getTelephone());
        structureDB.setCreation(structureDto.getCreation());

        structureDB.setType(this.typeStructureRepository.findById(structureDto.getId()).orElse(null));

        this.structureRepository.save(structureDB);

        return ResponseEntity.ok(new ServerReponse("Structure mis a jour",true));
    }

    @Override
    public ResponseEntity<ServerReponse> deleteStructure(Integer id) {
        this.structureRepository.findById(id);
        return ResponseEntity.ok(new ServerReponse("DELETE STRUCTURE : Failed",true));
    }
}
