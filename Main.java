import ui.Fenster;
import ui.util.UI;

import javax.swing.*;

public class Main {
    public static void main(String... args) {
        UI.initialise();
        SwingUtilities.invokeLater(Fenster::new);
    }
}