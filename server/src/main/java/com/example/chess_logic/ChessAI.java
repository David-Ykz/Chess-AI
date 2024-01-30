/**
 * Custom chess engine using minimax search
 * Optimizations used: alpha-beta pruning & move ordering, quiescence search
 * @author David Ye
 */

package com.example.chess_logic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.core.metrics.StartupStep;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ChessAI {
    // Performance Statistics //
    public int nodesSearched = 0;
    public int numPruned = 0;
    public EvaluationMap evalMap = new EvaluationMap();

    public ChessAI() {
    }

    public void printPerformanceInfo() {
        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Nodes Pruned: " + numPruned);
    }

    public int evaluate(Board board) {
        int totalEval = basicEval(board);
        int numPieces = 0;
        for (Piece piece : evalMap.pieceValueMap.keySet()) {
            numPieces += board.getPieceLocation(piece).size();
        }
        if (numPieces > 5 && Math.abs(totalEval) < 1000) {
            totalEval += openingEval(board);
        } else {
            totalEval += endgameEval(board);
        }
        return totalEval;
    }

    public int basicEval(Board board) {
        int totalEval = 0;
        for (Piece piece : evalMap.pieceValueMap.keySet()) {
            totalEval += board.getPieceLocation(piece).size() * evalMap.pieceValueMap.get(piece);
        }
        return totalEval;
    }

    public int openingEval(Board board) {
        int totalEval = 0;
        for (Piece piece : evalMap.pieceIntegerMap.keySet()) {
            totalEval += evalMap.openingEvaluation(board, piece) * (piece.getPieceSide() == Side.WHITE ? 1 : -1);
        }
        return totalEval;
    }

    public int endgameEval(Board board) {
        int totalEval = 0;
        if (basicEval(board) > 200) {
            totalEval += 470 * evalMap.findCMD(board.getKingSquare(Side.BLACK), -1);
            totalEval += 160 * (14 - evalMap.findMD(board));
        } else if (basicEval(board) < -200) {
            totalEval -= 470 * evalMap.findCMD(board.getKingSquare(Side.WHITE), 1);
            totalEval -= 160 * (14 - evalMap.findMD(board));
        }
        return totalEval;
    }

    public EvalMove maxMove(EvalMove a, EvalMove b) {
        return a.eval >= b.eval ? a : b;
    }
    public EvalMove minMove(EvalMove a, EvalMove b) {
        return a.eval <= b.eval ? a : b;
    }

    // Sorts the moves so better moves are searched first
    public List<EvalMove> orderMoves(Board board, List<Move> moves) {
        List<EvalMove> orderedMoves = new ArrayList<>(moves.size());
        for (Move move : moves) {
            int moveScore = 0;
            Piece movedPiece = board.getPiece(move.getFrom());
            Piece capturedPiece = board.getPiece(move.getTo());
            if (capturedPiece != Piece.NONE) {
                moveScore += Math.abs(evalMap.pieceValueMap.get((capturedPiece))) - Math.abs(evalMap.pieceValueMap.get(movedPiece))/10;
                if (movedPiece == Piece.WHITE_KING || movedPiece == Piece.BLACK_KING) {
                    moveScore += Math.abs(evalMap.pieceValueMap.get(movedPiece));
                }
            }

            Piece oppositeColorPawn = board.getSideToMove() == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
            if ((move.getTo().getBitboard() & board.getBitboard(oppositeColorPawn)) != 0L) {
                moveScore--;
            }

            if (move.getPromotion() != Piece.NONE) {
                moveScore += Math.abs(evalMap.pieceValueMap.get(move.getPromotion()));
            }
            orderedMoves.add(new EvalMove(move, 0, moveScore));
        }
        Collections.sort(orderedMoves);
        return orderedMoves;
    }

    public EvalMove quiescenceSearch(Board board, int alpha, int beta) {
        nodesSearched++;
        EvalMove bestMove = new EvalMove(evaluate(board));

        if (board.getSideToMove() == Side.WHITE) {
            alpha = Math.max(alpha, bestMove.eval);
        } else {
            beta = Math.min(beta, bestMove.eval);
        }
        if (beta <= alpha) {
            numPruned++;
            return bestMove;
        }

        List<Move> captureMoves = board.pseudoLegalCaptures();
        captureMoves.removeIf((move) -> {
            return !board.isMoveLegal(move, false);
        });
        if (captureMoves.isEmpty()) {
            return bestMove;
        }

        List<EvalMove> orderedMoves = orderMoves(board, captureMoves);
        for (EvalMove orderedMove : orderedMoves) {
            Move move = orderedMove.move;
            board.doMove(move);
            EvalMove newMove = new EvalMove(move, quiescenceSearch(board, alpha, beta).eval);
            board.undoMove();
            if (board.getSideToMove() == Side.WHITE) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.eval);
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.eval);
            }
            if (beta <= alpha) {
                return bestMove;
            }

        }
        return bestMove;
    }

    // Finds the best move by searching all legal moves down to a specified depth
    public EvalMove minimax(Board board, int depth, int alpha, int beta) {
        nodesSearched++;
        int turn = board.getSideToMove() == Side.WHITE ? 1 : -1;
        if (depth == 0) return quiescenceSearch(board, alpha, beta);
        List<Move> legalMoves = board.legalMoves();
        if (board.isMated()) {
            return new EvalMove((999999 + depth * 10000) * -turn);
        } else if (board.isDraw()){
            return new EvalMove(0);
        }

        EvalMove bestMove;
        if (turn > 0) {
            bestMove = new EvalMove(Integer.MIN_VALUE);
        } else {
            bestMove = new EvalMove(Integer.MAX_VALUE);
        }
        // Goes through each move trying to find the best move among them
        List<EvalMove> orderedMoves = orderMoves(board, legalMoves);
        for (EvalMove orderedMove : orderedMoves) {
            Move move = orderedMove.move;
            board.doMove(move);
            EvalMove newMove = new EvalMove(move, minimax(board, depth - 1, alpha, beta).eval);
            board.undoMove();
            // Compares and prunes the moves
            if (turn > 0) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.eval);
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.eval);
            }

            if (depth == 7) {
                System.out.println(newMove.move.getFrom() + " -> " + newMove.move.getTo() + " : " + newMove.eval);
            }

            if (beta <= alpha) {
                numPruned++;
                return bestMove;
            }
        }
        return bestMove;
    }

    public String queryEndgameTables(String fen) {
        String QUERY_URL = "http://tablebase.lichess.ovh/standard?fen=";
        fen = fen.replace(' ', '_');
        String strResponse = "";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(QUERY_URL + fen))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            strResponse = response.body();
        } catch (Exception e) {
            System.out.println(e);
        }
        return strResponse;
    }

    public EvalMove searchEndgameTables(Board board) {
        Move move = null;
        try {
            String fen = board.getFen();
            String response = queryEndgameTables(fen);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            String category = rootNode.get("category").asText();
            if (category.equals("unknown")) return new EvalMove(0);
            String uci = rootNode.get("moves").get(0).get("uci").asText();

            Square fromSquare = Square.fromValue(uci.toUpperCase().substring(0, 2));
            Square toSquare = Square.fromValue(uci.toUpperCase().substring(2, 4));
            if (uci.length() > 4) {
                Piece promotionPiece = Piece.fromFenSymbol(uci.substring(4));
                move = new Move(fromSquare, toSquare, promotionPiece);
            } else {
                move = new Move(fromSquare, toSquare);
            }
        } catch (Exception e) {
            System.out.println("Encountered error reading from table");
            System.out.println(e);
        }
        board.doMove(move);
        int eval = evaluate(board);
        board.undoMove();
        return new EvalMove(move, eval);
    }

    public EvalMove findMove(Board board) {
        EvalMove evalMove = searchEndgameTables(board);
        if (evalMove.move.getTo() != Square.NONE) {
            return evalMove;
        } else {
            evalMove = minimax(board, 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return evalMove;
    }
}