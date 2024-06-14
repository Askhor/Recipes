package ui.util;

import javax.swing.*;

/**
 * A utility class for various functions relating to ui
 * */
public class UI {
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            System.err.println("Error during startup. UI customisation seems to have failed");
            e.printStackTrace();
        }
    }
}