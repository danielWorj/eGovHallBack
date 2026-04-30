package com.example.eHall.Repository.Piece;

import com.example.eHall.Entity.Domaine.Structure;
import com.example.eHall.Entity.PieceJustificative.TypePiece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypePieceRepository extends JpaRepository<TypePiece,Integer> {
    List<TypePiece> findByStructure(Structure structure);

}
