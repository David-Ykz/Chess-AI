package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MoveGenerationTester {
    static int searchedPos = 0;
    public static void perft(int depth, com.github.bhlangonijr.chesslib.Board board) {
        searchedPos++;
        if (depth == 0) {
            return;
        }
        List<com.github.bhlangonijr.chesslib.move.Move> moves = board.legalMoves();
        for (com.github.bhlangonijr.chesslib.move.Move move : moves) {
            board.doMove(move);
            perft(depth - 1, board);
            board.undoMove();
        }
    }





    public static void main(String[] args) {
        ChessAI ai = new ChessAI();
        double start = System.nanoTime();
        double end = System.nanoTime();
        System.out.println("Time taken: " + (end - start)/1000000000);
        System.out.println(searchedPos);


        com.github.bhlangonijr.chesslib.Board newBoard = new com.github.bhlangonijr.chesslib.Board();
        searchedPos = 0;
        start = System.nanoTime();
        perft(4, newBoard);
        end = System.nanoTime();
        System.out.println("Time taken: " + (end - start)/1000000000);
        System.out.println(searchedPos);

        List<Square> squares = newBoard.getPieceLocation(Piece.WHITE_PAWN);
        for (Square sq : squares) {
            System.out.println(sq.toString().charAt(0));
        }
    }
}
