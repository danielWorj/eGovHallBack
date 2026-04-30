package com.example.eHall.Controller.Declaration;

import com.example.eHall.Entity.Server.ServerReponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/declaration")
@CrossOrigin("*")
public interface DeclarationControllerInt {

    /**
     * Crée une déclaration avec ses pièces justificatives.
     *
     * @param declaration  JSON stringifié du DeclarationDto
     * @param fichiers     Tableau de fichiers (peut être null si aucune pièce)
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ServerReponse> createDeclaration(
            @RequestPart("declaration") String declaration,
            @RequestPart(value = "fichiers", required = false) MultipartFile[] fichiers
    );
}