package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class MoveGenerationTester {
    static int searchedPos = 0;
    public static Board fenToBoard(String fen, int playerColor) {
        HashMap<Integer, Integer> pieces = new HashMap<>();
        HashMap<String, Integer> nameToValue = new HashMap<>();
        nameToValue.put("K", 6);
        nameToValue.put("Q", 5);
        nameToValue.put("R", 4);
        nameToValue.put("N", 3);
        nameToValue.put("B", 2);
        nameToValue.put("P", 1);
        nameToValue.put("k", -6);
        nameToValue.put("q", -5);
        nameToValue.put("r", -4);
        nameToValue.put("n", -3);
        nameToValue.put("b", -2);
        nameToValue.put("p", -1);
        // Iterates through the fen string and places pieces into a hashmap
        for (int i = 0; i < 8; i++) {
            String row;
            int buffer = 0;
            if (i < 7) {
                row = fen.substring(0, fen.indexOf('/'));
                fen = fen.substring(fen.indexOf('/') + 1);
            } else {
                row = fen.substring(0, fen.indexOf(' '));
                fen = fen.substring(fen.indexOf(' ') + 1);
            }

            int rowIndex = 0;
            for (int j = 0; j < 8; j++) {
                rowIndex++;
                if (buffer == 0) {
                    String character = row.substring(0, 1);
                    row = row.substring(1);
                    if (nameToValue.containsKey(character)) {
                        pieces.put(10 * rowIndex + i + 1, nameToValue.get(character));
                    } else {
                        buffer = Integer.parseInt(character) - 1;
                    }
                } else {
                    buffer--;
                }
            }
        }
        int turn;
        if (fen.substring(0, 1).equals("w")) {
            turn = 1;
        } else {
            turn = -1;
        }
        return new Board(turn, pieces, playerColor);
    }

    public static void perft(int depth, int color, Board board) {
        searchedPos++;
        if (depth == 0) {
            return;
        }
        HashSet<Move> moves = board.allLegalMoves(color);
        for (Move move : moves) {
            int capturedPiece = board.movePiece(move);
            perft(depth - 1, -color, board);
            board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
        }
    }

    public static void perft2(int depth, com.github.bhlangonijr.chesslib.Board board) {
        searchedPos++;
        if (depth == 0) {
            return;
        }
        List<com.github.bhlangonijr.chesslib.move.Move> moves = board.legalMoves();
        for (com.github.bhlangonijr.chesslib.move.Move move : moves) {
            board.doMove(move);
            perft2(depth - 1, board);
            board.undoMove();
        }
    }





    public static void main(String[] args) {
        ChessAI ai = new ChessAI();
        Board board = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1);
        System.out.println(board.toFEN());
        double start = System.nanoTime();
        perft(4, 1, board);
        double end = System.nanoTime();
        System.out.println("Time taken: " + (end - start)/1000000000);
        System.out.println(searchedPos);


        com.github.bhlangonijr.chesslib.Board newBoard = new com.github.bhlangonijr.chesslib.Board();
        searchedPos = 0;
        start = System.nanoTime();
        perft2(4, newBoard);
        end = System.nanoTime();
        System.out.println("Time taken: " + (end - start)/1000000000);
        System.out.println(searchedPos);

        List<Square> squares = newBoard.getPieceLocation(Piece.WHITE_PAWN);
        for (Square sq : squares) {
            System.out.println(sq.toString().charAt(0));
        }
    }
}
