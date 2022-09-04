/**
 * Displays the different pieces that a player can promote to
 * @author David Ye
 */

import javax.swing.*;
import java.awt.*;

public class PromotionMenu extends JFrame{
    JPanel panel;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    String[] names = {"Bishop", "Knight", "Rook", "Queen"};
    PromotionMenu(int color, int oldPosition, int newPosition) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(MAX_X / 4, MAX_Y / 8);
        // Start the frame in the center of the screen
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.background);
                g.fillRect(0, 0, MAX_X, MAX_Y);
                this.repaint();
            }
        };
        // Adds all the buttons for the different promotable pieces
        for (int i = 0; i < 4; i++) {
            JButton button = new JButton(names[i]);
            button.addActionListener(new PromotionButtonListener(this, i + 2, color, oldPosition, newPosition));
            panel.add(button);
            button.setSize(300, 100);
            button.setLocation(0, i * 300);
        }

        this.add(panel);
        this.setVisible(true);
        this.requestFocusInWindow();
    }
}