package com.example.eHall.Controller.Declaration;

import com.example.eHall.Entity.Acte.ActeNaissance;
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

    @GetMapping("/all/byhopital/{id}")
    ResponseEntity<List<Declaration>> findDeclarationByHopital(@PathVariable Integer id);

    @GetMapping("/all/bymairie/{id}")
    ResponseEntity<List<Declaration>> findDeclarationByMairie(@PathVariable Integer id);

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ServerReponse> createDeclaration(
            @RequestPart("declaration") String declaration,
            @RequestPart(value = "fichiers", required = false) MultipartFile[] fichiers
    );

    // ── Actes de naissance ────────────────────────────────────────────────

    @GetMapping("/acte/all/bymairie/{id}")
    ResponseEntity<List<ActeNaissance>> findAllActeNaissanceByMairie(@PathVariable Integer id);

    @PostMapping(value = "/acte/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ServerReponse> creationActeNaissance(
            @RequestPart("acte") String acte,
            @RequestPart(value = "fichiers", required = false) MultipartFile[] fichiers
    );

    /**
     * Met à jour un acte de naissance existant (informations du père + date de l'acte).
     *
     * @param id      identifiant de l'ActeNaissance à modifier
     * @param acte    JSON sérialisé de l'ActeDto
     * @param fichiers pièces jointes optionnelles
     */
    @PutMapping(value = "/acte/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ServerReponse> updateActeNaissance(
            @PathVariable Integer id,
            @RequestPart("acte") String acte,
            @RequestPart(value = "fichiers", required = false) MultipartFile[] fichiers
    );
}