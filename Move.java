public class Move {
    private int oldPosition;
    private int newPosition;
    private int capturedPiece;
    private double evaluation;

    public Move(int oldPosition, int newPosition) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    public Move(Move move, double evaluation) {
        this.oldPosition = move.getOldPosition();
        this.newPosition = move.getNewPosition();
        this.evaluation = evaluation;
    }

    public Move(int oldPosition, int newPosition, double evaluation) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.evaluation = evaluation;
    }


    public int getOldPosition() {
        return this.oldPosition;
    }

    public int getNewPosition() {
        return this.newPosition;
    }

    public int getCapturedPiece() {
        return this.capturedPiece;
    }

    public double getEvaluation() {
        return evaluation;
    }
}