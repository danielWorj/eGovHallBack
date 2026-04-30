package com.example.eHall.Controller.Domaine;


import com.example.eHall.Entity.Domaine.Structure;
import com.example.eHall.Entity.Server.ServerReponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/domaine")
@CrossOrigin("*")
public interface DomaineControllerInt {
    @GetMapping("/all")
    ResponseEntity<List<Structure>> findAllStructure();
    @PostMapping("/create")
    ResponseEntity<ServerReponse> createStructure(@Param("structure") String structure);
    @PostMapping("/update")
    ResponseEntity<ServerReponse> updateStructure(@Param("structure") String structure);
    @GetMapping("/delete/{id}")
    ResponseEntity<ServerReponse> deleteStructure(@PathVariable Integer id);

}
