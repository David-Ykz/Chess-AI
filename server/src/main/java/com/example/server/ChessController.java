package com.example.server;

import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.web.bind.annotation.*;
import com.example.chess_logic.*;
import com.github.bhlangonijr.chesslib.*;

import java.util.HashMap;
import org.json.*;

@RestController
public class ChessController {
    public HashMap<Integer, Board> gameBoards = new HashMap<>();

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
        EvalMove aiMove = chessAI.findMove(board);
        if (aiMove.move.getFrom() != Square.NONE) {
            board.doMove(aiMove.move);
        } else {
            System.out.println("Error: cannot make move");
        }
        double end = System.nanoTime();
        System.out.print("Time Taken: ");
        System.out.println((end-start)/1000000000);
        chessAI.printPerformanceInfo();
        System.out.println("Evaluation: " + aiMove.eval/100.0);
        return board.getFen();
    }

    @CrossOrigin(origins = {"http://localhost:3000",
            "https://bitboard.d18e1qx21kpcpk.amplifyapp.com/",
            "https://chessai.y-backend.com",
            "https://www.chessai.y-backend.com"})
    @GetMapping("/new-game")
    public int initializeNewGame() {
        int newId = 0;
        while (gameBoards.containsKey(newId)) {
            // random number from 0 to 99
            newId = (int)(Math.random() * 100);
        }
        gameBoards.put(newId, new Board());
        return newId;
    }

    @CrossOrigin(origins = {"http://localhost:3000",
            "https://bitboard.d18e1qx21kpcpk.amplifyapp.com/",
            "https://chessai.y-backend.com",
            "https://www.chessai.y-backend.com"})
    @PostMapping("/remove-game")
    public void removeExistingGame(@RequestBody int id) {
        // might have async issues if chessAI is currently processing board
        gameBoards.remove(id);
    }

    @CrossOrigin(origins = {"http://localhost:3000",
                            "https://bitboard.d18e1qx21kpcpk.amplifyapp.com/",
                            "https://chessai.y-backend.com",
                            "https://www.chessai.y-backend.com"})
    @PostMapping("/process-move")
    public String processRequest(@RequestBody String rawData) {
        try {
            JSONObject data = new JSONObject(rawData);
            int id = data.getInt("id");
            if (!gameBoards.containsKey(id))
                return "error";
            Board board = gameBoards.get(id);

            Square from = Square.valueOf(data.getJSONObject("move").getString("from").toUpperCase());
            Square to = Square.valueOf(data.getJSONObject("move").getString("to").toUpperCase());
            String color = data.getJSONObject("move").getString("color");
            String promotion = data.getJSONObject("move").getString("promotion");

            if (color.equals("w"))
                promotion = promotion.toUpperCase();
            Move move = new Move(from, to, Piece.fromFenSymbol(promotion));
            board.doMove(move);
            return makeMove(board);
        } catch (Exception e) {
            System.out.println("Encountered error");
            System.out.println(e);
            return "error";
        }
    }
}
