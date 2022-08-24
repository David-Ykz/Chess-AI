import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class OpeningMenu extends JFrame{
    JPanel panel;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    OpeningMenu() {
//        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(MAX_X / 2, MAX_Y / 2);
        this.setLocationRelativeTo(null); //start the frame in the center of the screen
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
        JButton white = new JButton("White");
        JButton black = new JButton("Black");
        JButton random = new JButton("Random");
        white.addActionListener(new WhiteButtonListener(this));
        black.addActionListener(new BlackButtonListener(this));
        random.addActionListener(new RandomButtonListener(this));
        panel.add(white);
        panel.add(black);
        panel.add(random);
        white.setLocation(0, 360);
        black.setLocation(0, 460);
        random.setLocation(0, 560);
        white.setSize(400, 100);
        black.setSize(400, 100);
        random.setSize(400, 100);

        this.add(panel);
        this.setVisible(true);
        this.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new OpeningMenu();
    }

}