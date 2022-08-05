import java.util.ArrayList;

public class StoredMove {
    int oldPosition;
    int newPosition;
    int capturedPiece;
    double evaluation;
    ArrayList<StoredMove> next = new ArrayList<>();

    public StoredMove(int oldPosition, int newPosition, int capturedPiece, double evaluation) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.capturedPiece = capturedPiece;
        this.evaluation = evaluation;
    }

    public void printInfo() {
        System.out.print("Old: " + oldPosition + " New: " + newPosition + " Captured: " + capturedPiece + " Eval: " + evaluation);
    }


}
