import java.util.*;

class ChessAI {

    ChessAI() {
    }
    public Move maxMove(Move a, Move b) {
        if (a.getEvaluation() > b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public Move minMove(Move a, Move b) {
        if (a.getEvaluation() < b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public HashSet<Move> generatePositionMoves(Board board, int color) {
        HashSet<Move> moves = new HashSet<>();
        HashSet<Integer> piecePositions = new HashSet<>(board.getPieces().keySet());
        for (Integer oldPosition : piecePositions) {
            if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : board.findLegalMoves(oldPosition)) {
                    moves.add(new Move(oldPosition, eachMove));
                }
            }
        }

        return moves;
    }

    public Move minmax(Board board, int depth, boolean isMaximizingPlayer, double alpha, double beta) {
        if (depth == 0) {
            return new Move(-1, -1, board.evaluateBoard());
        }

//        System.out.println(depth + " - " + isMaximizingPlayer);
        HashSet<Move> moves = new HashSet<>(generatePositionMoves(board, board.getTurn()));
        if (isMaximizingPlayer) {
            Move bestMove = new Move(-1, -1, -Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, false, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
                if (Math.abs(board.getPieces().get(move.getOldPosition())) != 6) {
                    System.out.println("Depth: " + depth + " Old pos: " + move.getOldPosition() + " New pos: " + move.getNewPosition() + " eval: " + newMove.getEvaluation());
                }

                bestMove = maxMove(bestMove, newMove);
//                System.out.println(depth + " - " + bestMove.getEvaluation());
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            Move bestMove = new Move(-1, -1, Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
                if (Math.abs(board.getPieces().get(move.getOldPosition())) != 6) {
                    System.out.println("Depth: " + depth + " Old pos: " + move.getOldPosition() + " New pos: " + move.getNewPosition() + " eval: " + newMove.getEvaluation());
                }

                bestMove = minMove(bestMove, newMove);
//                System.out.println(depth + " - " + bestMove.getEvaluation());
                beta = Math.min(beta, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        }
    }



    public Move minmaxStore(Board board, int depth, boolean isMaximizingPlayer, double alpha, double beta, StoredMove prev) {
        if (depth == 0) {
            prev.next.add(new StoredMove(-1, -1, -1, board.evaluateBoard()));
            return new Move(-1, -1, board.evaluateBoard());
        }



 //       System.out.println(depth + " " + isMaximizingPlayer);
        HashSet<Move> moves = new HashSet<>(generatePositionMoves(board, board.getTurn()));
        if (isMaximizingPlayer) {
            Move bestMove = new Move(-1, -1, -Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                StoredMove storeMove = new StoredMove(move.getOldPosition(), move.getNewPosition(), capturedPiece, 0);
                prev.next.add(storeMove);
                Move newMove = new Move(move, minmaxStore(board, depth - 1, false, alpha, beta, storeMove).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
                storeMove.evaluation = newMove.getEvaluation();
                if (board.getPieces().get(move.getOldPosition()) != 6) {
       //             System.out.println("Old pos: " + move.getOldPosition() + " New pos: " + move.getNewPosition() + " eval: " + newMove.getEvaluation());
                }

                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            Move bestMove = new Move(-1, -1, Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                StoredMove storeMove = new StoredMove(move.getOldPosition(), move.getNewPosition(), capturedPiece, 0);
                prev.next.add(storeMove);
                Move newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
                storeMove.evaluation = newMove.getEvaluation();
                if (board.getPieces().get(move.getOldPosition()) != -6) {
    //                System.out.println("Old pos: " + move.getOldPosition() + " New pos: " + move.getNewPosition() + " eval: " + newMove.getEvaluation());
                }

                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        }
    }






}