import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class BlackButtonListener extends StartButtonListener {
    JFrame parentFrame;
    BlackButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        parentFrame.dispose();
        new Chess(-1);
    }
}