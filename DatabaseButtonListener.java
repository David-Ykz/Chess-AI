import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class DatabaseButtonListener extends ButtonListener {
    JFrame parentFrame;
    DatabaseButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        OpeningMenu.loadDatabase = !OpeningMenu.loadDatabase;
    }
}