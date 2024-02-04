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
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import org.springframework.core.metrics.StartupStep;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ChessAI {
    // Performance Statistics //
    public int nodesSearched;
    public int numPruned;

    public Board board;
    public Evaluator evaluator;

    public final int PAWN_ATTACK_SQUARE_PENALTY = 30;
    public final long MAX_TIME_ALLOCATED = 1000;
    public final int ASPIRATION_WINDOW_WIDTH = 62;

    public EvaluationWeights evalWeights = new EvaluationWeights();
    public TranspositionTable transpositionTable = new TranspositionTable();
    public PVTable pvTable = new PVTable();

    public ChessAI(Board board) {
        this.board = board;
        this.evaluator = new Evaluator();

        this.nodesSearched = 0;
        this.numPruned = 0;

    }

    public void printPerformanceInfo() {
        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Nodes Pruned: " + numPruned);
    }

    public EvalMove maxMove(EvalMove a, EvalMove b) {
        return a.eval >= b.eval ? a : b;
    }
    public EvalMove minMove(EvalMove a, EvalMove b) {
        return a.eval <= b.eval ? a : b;
    }

    public List<EvalMove> orderMoves(List<Move> moves) {
        List<EvalMove> orderedMoves = new ArrayList<>(moves.size());
        for (Move move : moves) {
            int moveScore = 0;
            Piece movedPiece = board.getPiece(move.getFrom());
            Piece capturedPiece = board.getPiece(move.getTo());
            if (capturedPiece != Piece.NONE) {
                if (movedPiece == Piece.WHITE_KING || movedPiece == Piece.BLACK_KING) {
                    moveScore += Math.abs(evaluator.getMidgamePieceValue(capturedPiece));
                } else {
                    moveScore += Math.abs(evaluator.getMidgamePieceValue(capturedPiece)) - Math.abs(evaluator.getMidgamePieceValue(movedPiece))/10;
                }
            }

            Piece oppositeColorPawn = board.getSideToMove() == Side.WHITE ? Piece.BLACK_PAWN : Piece.WHITE_PAWN;
            if ((move.getTo().getBitboard() & board.getBitboard(oppositeColorPawn)) != 0L) {
                moveScore -= PAWN_ATTACK_SQUARE_PENALTY;
            }

            if (move.getPromotion() != Piece.NONE) {
                moveScore += Math.abs(evaluator.getMidgamePieceValue(move.getPromotion()));
            }

            board.doMove(move);
            EvalMove entry = pvTable.probe(board.getZobristKey());
            board.undoMove();
            if (entry != null) {
                orderedMoves.add(new EvalMove(move, 0, moveScore + Math.abs(entry.eval)));
            } else {
                orderedMoves.add(new EvalMove(move, 0, moveScore));
            }
        }
        Collections.sort(orderedMoves);
        return orderedMoves;
    }

    public EvalMove quiescenceSearch(int alpha, int beta) {
        nodesSearched++;
        EvalMove bestMove = new EvalMove(evaluator.evaluate(board));

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

        List<EvalMove> orderedMoves = orderMoves(captureMoves);
        for (EvalMove orderedMove : orderedMoves) {
            Move move = orderedMove.move;
            board.doMove(move);
            EvalMove newMove = new EvalMove(move, quiescenceSearch(alpha, beta).eval);
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

    public EvalMove minimax(int depth, int alpha, int beta) {
        nodesSearched++;
        int turn = board.getSideToMove() == Side.WHITE ? 1 : -1;

        if (depth == 0)
            return quiescenceSearch(alpha, beta);

        List<Move> legalMoves = board.legalMoves();

        EvalMove bestMove = new EvalMove(turn > 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);

        List<EvalMove> orderedMoves = orderMoves(legalMoves);
        for (EvalMove orderedMove : orderedMoves) {
            Move move = orderedMove.move;
            board.doMove(move);
            if (board.isMated()) {
                orderedMove.eval = (999999 + depth * 10000) * turn;
            } else if (board.isDraw()){
                orderedMove.eval = 0;
            } else {
                orderedMove.eval = minimax(depth - 1, alpha, beta).eval;
            }
            board.undoMove();

            if (turn > 0) {
                bestMove = maxMove(bestMove, orderedMove);
                alpha = Math.max(alpha, orderedMove.eval);
            } else {
                bestMove = minMove(bestMove, orderedMove);
                beta = Math.min(beta, orderedMove.eval);
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
        int eval = evaluator.evaluate(board);
        board.undoMove();
        return new EvalMove(move, eval);
    }

    public EvalMove findMove(Board board) {
        EvalMove bestMove = searchEndgameTables(board);

        if (bestMove.move.getTo() != Square.NONE) {
            return bestMove;
        } else {
            long startTime = System.currentTimeMillis();

            for (int depth = 1; depth < 100; depth++) {
                System.out.println("Searching from depth " + depth);
                int alpha = Integer.MIN_VALUE;
                int beta = Integer.MAX_VALUE;
                if (depth > 1) {
                    alpha = bestMove.eval - ASPIRATION_WINDOW_WIDTH;
                    beta = bestMove.eval + ASPIRATION_WINDOW_WIDTH;
                }

                EvalMove currentMove = minimax(depth, alpha, beta);

                if (Math.abs(bestMove.eval - currentMove.eval) > ASPIRATION_WINDOW_WIDTH) {
                    System.out.println("Researching move");
                    currentMove = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                }

                board.doMove(currentMove.move);
                pvTable.store(board.getZobristKey(), currentMove);
                board.undoMove();
                if (bestMove.move.getTo() == Square.NONE ||
                        (board.getSideToMove() == Side.WHITE && currentMove.eval >= bestMove.eval) ||
                        (board.getSideToMove() == Side.BLACK && currentMove.eval <= bestMove.eval)) {
                        bestMove = currentMove;
                }

                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime >= MAX_TIME_ALLOCATED) {
                    break;
                }
            }
        }
        return bestMove;
    }
}