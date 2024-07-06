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
import java.util.function.Consumer;

/**
 * Das Menü, das dann halt immer an der Seite rumhängt
 */
public class SideMenu extends JPanel {

    private Rezept selectedRezept = null;

    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public SideMenu(boolean newRecipe, boolean deleteRecipe, boolean newWindow, boolean backToMenu, boolean exportAsHtml, boolean edit, boolean view, CustomButton... customButtons) {
        if (view)
            add(new JButton(new SimpleAction("View", this::view)));
        if (edit)
            add(new JButton(new SimpleAction("Edit", this::edit)));
        if (newWindow)
            add(new JButton(new SimpleAction("Neues Fenster", this::neuesFenster)));
        if (newRecipe)
            add(new JButton(new SimpleAction("Neues Rezept", this::neuesRezept)));
        if (deleteRecipe)
            add(new JButton(new SimpleAction("Rezept Löschen", this::rezeptLoschen)));

        add(Box.createVerticalStrut(20));

        if (exportAsHtml)
            add(new JButton(new SimpleAction("Export als HTML", this::htmlExport)));

        for (CustomButton c : customButtons)
            add(new JButton(new SimpleAction(c.name, () -> c.action.accept(selectedRezept))));

        add(Box.createVerticalGlue());

        if (backToMenu)
            add(new JButton(new SimpleAction("Zurück zum Menu", this::zumMenu)));

    }

    private void view() {
        if (selectedRezept == null) {
            JOptionPane.showMessageDialog(this, "Wähle ein Rezept aus, um es anzuzeigen");
            return;
        }

        if (!(SwingUtilities.windowForComponent(this) instanceof Fenster f)) {
            System.err.println("SideMenu wurde dazu konstruiert, in einem Fenster verwendet zu sein");
            return;
        }

        f.setContent(new RezeptViewer(selectedRezept));
    }

    private void edit() {
        if (selectedRezept == null) {
            JOptionPane.showMessageDialog(this, "Wähle ein Rezept aus, um es zu editieren");
            return;
        }
        if (RezeptEditor.wirdEditiert(selectedRezept)) {
            RezeptEditor.getEditor(selectedRezept).requestFocus();
            return;
        }

        if (!(SwingUtilities.windowForComponent(this) instanceof Fenster f)) {
            System.err.println("SideMenu wurde dazu konstruiert, in einem Fenster verwendet zu sein");
            return;
        }

        f.setContent(new RezeptEditor(selectedRezept));
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
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Das Rezept konnte nicht als HTML-datei exportiert werden");
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

        int ergebnis = JOptionPane.showConfirmDialog(this, "Willst du wirklich " + selectedRezept.getName() + " löschen?", "Rezepte fragen nach:", JOptionPane.OK_CANCEL_OPTION);

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
        new Fenster(new RezeptViewer(selectedRezept));
    }


    public void setSelectedRezept(Rezept rezept) {
        this.selectedRezept = rezept;
    }

    public record CustomButton(String name, Consumer<Rezept> action) {
    }
}