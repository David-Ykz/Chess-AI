package com.example.server;

//import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.web.bind.annotation.*;
import com.example.chess_logic.*;
import com.github.bhlangonijr.chesslib.*;


@RestController
public class ChessController {
    public String processJSFen(String str) {
        str = str.replace("%2F", "/");
        str = str.replace("+", " ");
        str = str.replace("=", "");
        return str;
    }
    public Board fenToBoard(String str) {
        Board board = new Board();
        str = processJSFen(str);
        System.out.println(str);
        board.loadFromFen(str);
        return board;
    }
    public String makeMove(Board board) {
        ChessAI chessAI = new ChessAI();
        double start = System.nanoTime();
        EvalMove aiMove = chessAI.minmax(board, 3, Integer.MAX_VALUE, Integer.MIN_VALUE);
        if (aiMove.move.getFrom() != Square.NONE) {
            board.doMove(aiMove.move);
        } else {
            System.out.println("Error: cannot make move");
        }
        double end = System.nanoTime();
        System.out.print("Time Taken: ");
        System.out.println((end-start)/1000000000);
        return board.getFen();
    }
//    @CrossOrigin(origins = "https://main.d3kqvs59i8mifl.amplifyapp.com/")
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/move")
    public String processRequest(@RequestBody String fen) {
        String output = makeMove(fenToBoard(fen));
        System.out.println("Output: " + output);
        System.out.println();
        return output;
    }
}
