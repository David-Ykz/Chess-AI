import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class RandomButtonListener extends ButtonListener {
    JFrame parentFrame;
    RandomButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        parentFrame.dispose();
        if (Math.random() > 0.5) {
            new Chess(1);
        } else {
            new Chess(-1);
        }
    }
}