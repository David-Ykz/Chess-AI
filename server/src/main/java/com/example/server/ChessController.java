package com.example.server;

import org.springframework.web.bind.annotation.*;
import com.example.chess_logic.*;


@RestController
public class ChessController {
    public Board exchangeStringToBoard(String str) {
        return new Board(str, "%2F");
    }
    public String fenAfterAIMove(Board board) {
        ChessAI chessAI = new ChessAI();
        double start = System.nanoTime();
        Move aiMove = chessAI.findMove(board);
        if (aiMove.getOldPosition() != -1) {
            board.makeMove(aiMove);
        }
        double end = System.nanoTime();
        return board.toFEN();
    }
    @CrossOrigin(origins = "https://main.d3kqvs59i8mifl.amplifyapp.com/")
    @PostMapping("/move")
    public String processRequest(@RequestBody String fen) {
        String output = fenAfterAIMove(exchangeStringToBoard(fen));
        System.out.println("Output: " + output);
        System.out.println();
        return output;
    }
}
