import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ChessVisualizer extends JFrame{
    GamePanel panel;
    Board board;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    static final int GRIDSIZE = 67;
    final int BOARD_SIZE = GRIDSIZE * 8;
    ChessVisualizer(Board board) {
        MyMouseListener mouseListener = new MyMouseListener();
        this.addMouseListener(mouseListener);
        this.board = board;
        this.panel = new GamePanel();
        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(BOARD_SIZE + 400, BOARD_SIZE + 40);
        this.setVisible(true);
    }
    private class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color background = new Color(80, 80, 80);
            g.fillRect(0, 0, MAX_X, MAX_Y);
            board.drawBoard(g, GRIDSIZE);
            Chess.displayInfo(g, BOARD_SIZE + 5, BOARD_SIZE + 10);
//        board.drawEvaluation(g, GRIDSIZE);
            this.repaint();
            if (board.isCheckmate()) {
                g.setFont (new Font ("SansSerif", Font.BOLD | Font.PLAIN, 75));
                g.setColor(new Color(0, 0, 0));
                g.drawString("Checkmate", 5, BOARD_SIZE + 5);
            }
        }




    }
}