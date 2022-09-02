import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

abstract class ButtonListener implements ActionListener {
    JFrame parentFrame;
    ButtonListener(JFrame parent) {
        parentFrame = parent;
    }
    // The action performed will vary based on what button the user clicks (what player class gets created during the game)
    public abstract void actionPerformed(ActionEvent event);
}