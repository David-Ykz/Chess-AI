/**
 * Stores a move, with the old position, new position, evaluation of the move, and the promoted piece for the move
 * @author David Ye
 */

package com.example.chess_logic;
public class Move implements Comparable<Move> {
    private int oldPosition;
    private int newPosition;
    private int promotedPiece = 0;
    private double evaluation;
    private int moveScore;

    public Move(int oldPosition, int newPosition) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
    public Move (int oldPosition, int newPosition, int promotedPiece) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.promotedPiece = promotedPiece;
    }
    public Move(Move move, double evaluation) {
        this.oldPosition = move.getOldPosition();
        this.newPosition = move.getNewPosition();
        this.promotedPiece = move.getPromotedPiece();
        this.evaluation = evaluation;
    }
    public Move(int oldPosition, int newPosition, double evaluation) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.evaluation = evaluation;
    }

    public int getOldPosition() { return this.oldPosition; }
    public int getNewPosition() { return this.newPosition; }
    public double getEvaluation() { return this.evaluation; }
    public int getPromotedPiece() { return this.promotedPiece; }
    public void setEvaluation(double evaluation) { this.evaluation = evaluation; }
    public void setMoveScore(int score) { this.moveScore = score; }

    @Override
    // Uses a compare to for move score when ordering moves
    public int compareTo(Move move) {
        if (this.moveScore > move.moveScore) {
            return -1;
        } else if (this.moveScore < move.moveScore) {
            return 1;
        } else {
            return 0;
        }
    }
}
