package ui;

import ui.components.Content;
import ui.components.MainMenu;

import javax.swing.*;
import java.awt.*;

/**
 * Das Applikations-Fenster
 */
public class Fenster extends JFrame {
    private Content content;

    {
        setSize(720, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public Fenster(Content c) {
        setContent(c);
    }

    public Fenster() {
        this(new MainMenu());
    }

    public void setContent(Content c) {
        if (content != null) {
            remove(content);
            content.close();
        }

        content = c;
        add(content, BorderLayout.CENTER);
        validate();

        updateTitle();
    }

    public void updateTitle() {
        setTitle("Rezeptbuch - " + content.getTitle());
    }
}