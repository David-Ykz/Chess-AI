/**
 * Displays the opening menu for the player upon running the program
 * Gives the option to select color and database preferences
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.*;
import java.awt.*;

public class OpeningMenu extends JFrame{
    JPanel panel;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    public static boolean loadDatabase = true;
    JButton database;
    OpeningMenu() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Colors.background);
                g.fillRect(0, 0, MAX_X, MAX_Y);
                if (loadDatabase) {
                    database.setText("Disable");
                } else {
                    database.setText("Enable");
                }
                this.repaint();
            }
        };
        panel.setLayout(null);
        JLabel title = new JLabel("Chess AI");
        title.setForeground(Colors.textWhite);
        title.setFont(new Font("Verdana",1,32));
        title.setBounds(110,0, 200,50);
        panel.add(title);
        // Buttons for player color
        JButton white = new JButton("White");
        JButton black = new JButton("Black");
        JButton random = new JButton("Random");
        white.setBounds(22,75, 100,30);
        black.setBounds(144,75, 100,30);
        random.setBounds(266,75, 100,30);
        white.addActionListener(new WhiteButtonListener(this));
        black.addActionListener(new BlackButtonListener(this));
        random.addActionListener(new RandomButtonListener(this));
        panel.add(white);
        panel.add(black);
        panel.add(random);
        // Button for database preference
        database = new JButton("Disable");
        database.setBounds(290,175, 80,30);
        database.addActionListener(new DatabaseButtonListener(this));
        panel.add(database);
        // Description of the database button
        JLabel description = new JLabel("Enables/Disables opening database");
        description.setForeground(Colors.textWhite);
        description.setFont(new Font("Verdana", Font.PLAIN,10));
        description.setBounds(10,160, 300,50);
        panel.add(description);
        JLabel warning = new JLabel("Enabling database may take some time to load");
        warning.setForeground(Colors.textWhite);
        warning.setFont(new Font("Verdana",Font.PLAIN,10));
        warning.setBounds(10,172, 400,50);
        panel.add(warning);

        this.add(panel);
        this.setVisible(true);
        this.requestFocusInWindow();
        this.setSize(400, 300);
        // Start the frame in the center of the screen
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new OpeningMenu();
    }

}