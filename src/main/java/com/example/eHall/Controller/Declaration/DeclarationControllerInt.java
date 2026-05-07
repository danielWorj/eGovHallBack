package com.example.eHall.Controller.Declaration;

import com.example.eHall.Entity.Acte.Declaration;
import com.example.eHall.Entity.Server.ServerReponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/eHall/api/declaration")
@CrossOrigin("*")
public interface DeclarationControllerInt {

    @GetMapping("/all")
    ResponseEntity<List<Declaration>> findDeclaration();
    @GetMapping("/all/bystructure/{id}")
    ResponseEntity<List<Declaration>> findDeclarationByEtablissement(@PathVariable Integer id);
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ServerReponse> createDeclaration(
            @RequestPart("declaration") String declaration,
            @RequestPart(value = "fichiers", required = false) MultipartFile[] fichiers
    );
}