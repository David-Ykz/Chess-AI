package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class EvalMove implements Comparable<EvalMove> {
    public Move move;
    public int eval;
    public int score;
    public EvalMove response = null;

    EvalMove(int eval) {
        move = new Move(Square.NONE, Square.NONE);
        this.eval = eval;
    }


    EvalMove(Move move, int eval) {
        this.move = move;
        this.eval = eval;
    }

    EvalMove(Move move, int eval, int score) {
        this.move = move;
        this.eval = eval;
        this.score = score;
    }


    @Override
    public int compareTo(EvalMove move) {
        if (this.score == move.score) {
            return 0;
        } else {
            return this.score > move.score ? -1 : 1;
        }
    }

}
