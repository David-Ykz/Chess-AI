package com.example.server;

import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.web.bind.annotation.*;
import com.example.chess_logic.*;
import com.github.bhlangonijr.chesslib.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.*;

@RestController
public class ChessController {
    public HashMap<Integer, GameInstance> gameInstances = new HashMap<>();

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
        final int MAX_CONCURRENT_GAMES = 100;
        System.out.println(gameInstances.keySet().toString());
        int newId = 0;

        HashSet<Integer> ids = new HashSet<>(gameInstances.keySet());
        for (int id : ids) {
            if (gameInstances.get(id).exceededLifetime()) {
                System.out.println("Removed Id: " + id);
                gameInstances.remove(id);
            }
        }

        if (gameInstances.size() == MAX_CONCURRENT_GAMES)
            return -1;

        while (gameInstances.containsKey(newId)) {
            newId = (int)(Math.random() * MAX_CONCURRENT_GAMES);
        }
        gameInstances.put(newId, new GameInstance());
        return newId;
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
            if (!gameInstances.containsKey(id))
                return "error";
            Board board = gameInstances.get(id).board;

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
