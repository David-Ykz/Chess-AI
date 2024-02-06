package com.example.server;

import com.example.chess_logic.ChessAI;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

import java.time.Duration;
import java.time.LocalDateTime;

public class GameInstance {
    public final long instanceLifetimeLimit = 3; // 3 hours
    public LocalDateTime createDate;
    public ChessAI ai;

    GameInstance(String playerSide) {
        this.createDate = LocalDateTime.now();
        this.ai = new ChessAI(new Board());

//        ai.board.loadFromFen("r4rk1/1bp1npp1/p2p1n1p/1p2p2P/4P2N/1BPPB1Pq/P1P2P2/1R1Q1RK1 b - - 2 15");
        if (playerSide.equals("black")) {
            ai.makeAIMove();
        }
    }

    public boolean exceededLifetime() {
        return Duration.between(createDate, LocalDateTime.now()).toHours() >= instanceLifetimeLimit;
    }

    public String getFen() {
        return ai.board.getFen();
    }

    public String undoMove() {
        ai.board.undoMove();
        ai.board.undoMove();
        return ai.board.getFen();
    }

    public String doMove(Square from, Square to, String promotion) {
        Move move;

        if (ai.board.getPiece(from) == Piece.WHITE_PAWN && to.getRank() == Rank.RANK_8){
            move = new Move(from, to, Piece.fromFenSymbol(promotion.toUpperCase()));
        } else if (ai.board.getPiece(from) == Piece.BLACK_PAWN && to.getRank() == Rank.RANK_1) {
            move = new Move(from, to, Piece.fromFenSymbol(promotion));
        } else {
            move = new Move(from, to);
        }

        ai.board.doMove(move);
        ai.makeAIMove();
        return ai.board.getFen();
    }
}
