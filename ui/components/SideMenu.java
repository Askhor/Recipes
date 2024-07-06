package ui.components;

import data.Rezept;
import data.html.HtmlConverter;
import ui.Fenster;
import ui.util.SimpleAction;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Das Menü, das dann halt immer an der Seite rumhängt
 */
public class SideMenu extends JPanel {

    private Rezept selectedRezept = null;

    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public SideMenu(boolean newRecipe, boolean deleteRecipe, boolean newWindow, boolean backToMenu, boolean exportAsHtml) {
        if (newRecipe)
            add(new JButton(new SimpleAction("Neues Rezept", this::neuesRezept)));
        if (deleteRecipe)
            add(new JButton(new SimpleAction("Rezept Löschen", this::rezeptLoschen)));
        if (newWindow)
            add(new JButton(new SimpleAction("Neues Fenster", this::neuesFenster)));
        if (exportAsHtml)
            add(new JButton(new SimpleAction("Export als HTML", this::htmlExport)));
        if (backToMenu)
            add(new JButton(new SimpleAction("Zurück zum Menu", this::zumMenu)));

        add(Box.createVerticalBox());
    }

    private void htmlExport() {
        if (selectedRezept == null) {
            JOptionPane.showMessageDialog(this, "Wähle ein Rezept aus, um es zu exportieren");
            return;
        }

        try {
            Path file = Files.createTempFile(selectedRezept.getName(), ".html");
            Files.writeString(file, HtmlConverter.convert(selectedRezept));
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file.toFile());
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Die HTML-Datei wurde gespeichert unter " + file.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void zumMenu() {
        Window window = SwingUtilities.windowForComponent(this);
        if (!(window instanceof Fenster fenster)) {
            System.err.println("SideMenu wurde dazu konstruiert, in einem Fenster verwendet zu sein");
            return;
        }

        fenster.setContent(new MainMenu());
    }

    private void neuesRezept() {
        Window window = SwingUtilities.windowForComponent(this);
        if (!(window instanceof Fenster fenster)) {
            System.err.println("SideMenu wurde dazu konstruiert, in einem Fenster verwendet zu sein");
            return;
        }

        fenster.setContent(new RezeptEditor(leeresRezept()));
    }

    private Rezept leeresRezept() {
        return new Rezept();
    }

    private void rezeptLoschen() {
        if (selectedRezept == null) {
            JOptionPane.showMessageDialog(this, "Wähle ein Rezept aus, um es zu löschen");
            return;
        }

        int ergebnis = JOptionPane.showConfirmDialog(this, "Willst du wirklich " + selectedRezept.getName() + " löschen?");

        switch (ergebnis) {
            case JOptionPane.OK_OPTION -> selectedRezept.loschen();
            case JOptionPane.CANCEL_OPTION -> {
            }
            default -> throw new Error("JOptionPane hat was seltsames gemacht");
        }
    }

    private void neuesFenster() {
        if (selectedRezept == null) {
            new Fenster();
            return;
        }

        if (RezeptEditor.wirdEditiert(selectedRezept)) {
            new Fenster();
        } else {
            new Fenster(new RezeptEditor(selectedRezept));
        }
    }


    public void setSelectedRezept(Rezept rezept) {
        this.selectedRezept = rezept;
    }
}