import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ChessVisualizer extends JFrame{
    GamePanel panel;
    Board board;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    //    final int GRIDSIZE = MAX_Y/16;
    static final int GRIDSIZE = 67;
    ChessVisualizer(Board board) {
        MyMouseListener mouseListener = new MyMouseListener();
        this.addMouseListener(mouseListener);
        this.board = board;
        this.panel = new GamePanel();
        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(MAX_X/2, 2*MAX_Y/3);
        this.setVisible(true);
    }
    private class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            board.drawBoard(g, GRIDSIZE);
//        board.drawEvaluation(g, GRIDSIZE);
            this.repaint();
            if (board.isCheckmate()) {
                g.setFont (new Font ("TimesRoman", Font.BOLD | Font.PLAIN, 75));
                g.setColor(new Color(0, 0, 0));
                g.drawString("Checkmate", 5, 2 * MAX_Y/3 - 120);
            }
        }




    }
}