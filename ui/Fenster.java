package ui;

import ui.components.Content;
import ui.components.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Das Applikations-Fenster
 */
public class Fenster extends JFrame {
    private Content content;

    {
        setSize(720, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (content == null) return;
                content.close();
            }
        });
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