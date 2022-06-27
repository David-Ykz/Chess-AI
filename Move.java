public class Move {
    private int oldPosition;
    private int newPosition;
    private double evaluation;

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

    public double getEvaluation() {
        return evaluation;
    }
}
