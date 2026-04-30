package com.example.eHall.Repository.Piece;

import com.example.eHall.Entity.PieceJustificative.PieceJustificative;
import com.example.eHall.Entity.PieceJustificative.TypePiece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Integer> {
    List<PieceJustificative> findByType(TypePiece typePiece);
}
