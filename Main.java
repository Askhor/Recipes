import ui.Fenster;
import ui.components.Content;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;

public class Main {
    public static void main(String... args) {
        UI.initialise();
        SwingUtilities.invokeLater(Fenster::new);
    }
}