import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


class MyMouseListener implements MouseListener {

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            int xSquare = e.getX()/ChessVisualizer.GRIDSIZE + 1;
            int ySquare = (e.getY() - 30)/ChessVisualizer.GRIDSIZE + 1;

            Chess.processClick(xSquare * 10 + ySquare, Chess.currentBoard);
        } else if (e.getButton() == 3) {
//            Chess.makeAIMove();
        }

    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}