package ui.components;

import data.Einheit;
import data.Rezept;
import data.ZutatInfo;
import ui.Fenster;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

/**
 * Der tatsächliche Rezept-Editor
 */
public class RezeptEditor extends Content {
    /**
     * Die Menge aller Rezepte, die gerade editiert werden
     */
    private static final HashMap<Rezept, RezeptEditor> rezepteImEditor = new HashMap<>();

    private final Rezept rezept;

    public RezeptEditor(Rezept rezept) {
        if (wirdEditiert(rezept)) {
            throw new IllegalStateException("Ein Rezept darf nicht zwei mal gleichzeitig editiert werden");
        }
        rezepteImEditor.put(rezept, this);
        this.rezept = rezept;

        setLayout(new BorderLayout());
        add(menu = new SideMenu(true, true, true, true, true, false, true), BorderLayout.WEST);
        menu.setSelectedRezept(rezept);

        populateComponents();

        Rezept.addPropertyListener(evt -> {
            if (!evt.getPropertyName().equals("Rezepte")) return;

            if (evt.getOldValue() == rezept && evt.getNewValue() == null && rezept.istGeloscht()) {
                if (SwingUtilities.windowForComponent(this) instanceof Fenster f) {
                    f.setContent(new MainMenu());
                }
            }
        });
    }

    /**
     * Fügt noch die ganzen Hauptkomponenten ein
     */
    private void populateComponents() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(main);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        Titel titel = new Titel();
        Kategorien kategorien = new Kategorien();
        Zutaten zutaten = new Zutaten();
        Beschreibung beschreibung = new Beschreibung();

        main.add(Box.createVerticalStrut(20));

        main.add(UI.createLine(Box.createHorizontalStrut(20), new JLabel("Titel:"), Box.createHorizontalStrut(7), titel));

        main.add(Box.createVerticalStrut(10));

        main.add(UI.createLine(kategorien));

        main.add(Box.createVerticalStrut(20));

        main.add(UI.createLine(zutaten));

        main.add(Box.createVerticalStrut(10));

        main.add(beschreibung);

        main.add(Box.createVerticalGlue());
    }

    @Override
    public String getTitle() {
        String name = rezept.getName();
        if (name.isBlank()) {
            return "Unbenanntes Rezept";
        }
        return name;
    }

    @Override
    public void close() {
        rezepteImEditor.remove(rezept);
    }

    /**
     * Ob das Rezept in einem Editor offen ist
     */
    public static boolean wirdEditiert(Rezept rezept) {
        return rezepteImEditor.containsKey(rezept);
    }


    /**
     * Falls das Rezept gerade editiert wird, wird der dazugehörige Editor zurückgegeben, ansonsten null
     */
    public static RezeptEditor getEditor(Rezept rezept) {
        return rezepteImEditor.get(rezept);
    }

    private class Titel extends JTextField {
        {
            setText(rezept.getName());

            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    onChange();
                }
            });
        }

        private void onChange() {
            try {
                rezept.setName(getText());
                setForeground(Color.BLACK);

                if (SwingUtilities.windowForComponent(this) instanceof Fenster f) {
                    f.updateTitle();
                }
            } catch (IllegalArgumentException e) {
                setForeground(new Color(200, 0, 0));
            }
        }
    }

    private class Kategorien extends JPanel {

        {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));


            for (data.Kategorie k : rezept.getKategorien()) {
                add(new Kategorie(k, -1));
            }

            add(Box.createHorizontalStrut(10));

            add(new NeueKategorie());
            add(Box.createGlue());
        }

        private abstract static class Element extends JButton {
            public Element(Action a, Color borderColor) {
                super(a);

                setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(borderColor, 2, true),
                                BorderFactory.createEmptyBorder(4, 4, 4, 4)
                        )
                );

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1 -> linksClick();
                            case MouseEvent.BUTTON2 -> mittelClick();
                            case MouseEvent.BUTTON3 -> rechtsClick();
                        }
                    }
                });
            }

            protected void linksClick() {

            }

            protected void mittelClick() {

            }

            protected void rechtsClick() {

            }
        }

        private class Kategorie extends Element {
            private final data.Kategorie k;
            private final Component spacing;

            public Kategorie(data.Kategorie k, int index) {
                super(new SimpleAction(k.getName(), () -> {
                }), Color.DARK_GRAY);
                this.k = k;

                JPopupMenu contextMenu = new JPopupMenu(k.getName());

                contextMenu.add("Kategorie " + k.getName());
                contextMenu.add(new SimpleAction("Entfernen", this::entfernen));

                setComponentPopupMenu(contextMenu);

                spacing = Box.createHorizontalStrut(10);
                if (index == -1) {
                    Kategorien.this.add(spacing);
                } else {
                    Kategorien.this.add(spacing, index);
                }
            }

            private void entfernen() {
                Kategorien.this.remove(this);
                Kategorien.this.remove(spacing);
                rezept.removeKategorie(k);
                SwingUtilities.windowForComponent(Kategorien.this).validate();
            }
        }

        private class NeueKategorie extends Element {
            public NeueKategorie() {
                super(new SimpleAction("Neue Kategorie", () -> {
                }), new Color(0, 180, 0));
            }

            @Override
            protected void linksClick() {
                String input = JOptionPane.showInputDialog("Gib den Namen für die neue Kategorie ein");
                if (input == null || input.isBlank()) {
                    JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Es wurde kein gescheiter Name eingegeben!");
                    return;
                }

                data.Kategorie k = data.Kategorie.get(input);

                if (rezept.getKategorien().contains(k)) return;
                rezept.addKategorie(k);


                Kategorien.this.add(
                        new Kategorien.Kategorie(k, Kategorien.this.getComponentCount() - 3),
                        Kategorien.this.getComponentCount() - 3
                );

                if (SwingUtilities.windowForComponent(this) instanceof Fenster f) {
                    f.validate();
                }
            }
        }
    }

    private class Zutaten extends JPanel {
        private final JPanel list = new JPanel();
        private final JPanel buttonPanel = new JPanel();

        {
            setLayout(new BorderLayout());

            add(list, BorderLayout.CENTER);
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            for (ZutatInfo z : rezept.getZutaten()) {
                list.add(new Zutat(z));
            }

            add(buttonPanel, BorderLayout.SOUTH);
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.add(new JButton(new SimpleAction("+", this::addNew)));
            buttonPanel.add(Box.createGlue());
        }

        private void addNew() {
            ZutatInfo zutat = new ZutatInfo();
            rezept.addZutat(zutat);
            list.add(new Zutat(zutat));
            SwingUtilities.windowForComponent(this).validate();
        }

        private class Zutat extends JPanel {
            private final ZutatInfo zutat;

            private final JTextField mengenInput = new JTextField(5);
            private final JComboBox<Einheit> einheitInput = new JComboBox<>(Einheit.values());
            private final JTextField zutatInput = new JTextField();
            private final JTextField notizenInput = new JTextField();


            public Zutat(ZutatInfo zutat) {
                this.zutat = zutat;

                mengenInput.setText("" + zutat.getMenge());
                einheitInput.setSelectedItem(zutat.getZutat().getEinheit());
                zutatInput.setText(zutat.getZutat().name());
                notizenInput.setText(zutat.getNotizen());

                mengenInput.setHorizontalAlignment(JTextField.RIGHT);

                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

                add(Box.createHorizontalStrut(20));

                add(mengenInput);
                add(einheitInput);
                add(zutatInput);
                add(Box.createHorizontalStrut(40));
                add(notizenInput);

                add(Box.createHorizontalGlue());

                mengenInput.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        onMengenInputChange();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        onMengenInputChange();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        onMengenInputChange();
                    }
                });
                einheitInput.addActionListener(this::onEinheitInputChange);
                zutatInput.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        onZutatInputChange();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        onZutatInputChange();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        onZutatInputChange();
                    }
                });
                notizenInput.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        onNotizenInputChange();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        onNotizenInputChange();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        onNotizenInputChange();
                    }
                });

                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(new SimpleAction("Entfernen", () -> {
                    rezept.removeZutat(zutat);
                    list.remove(this);
                    SwingUtilities.windowForComponent(Zutaten.this).validate();
                }));
                setComponentPopupMenu(contextMenu);

                for (Component c : getComponents()) {
                    if (c instanceof JComponent jc) jc.setInheritsPopupMenu(true);
                }
            }

            private void onZutatInputChange() {
                if (zutat.getZutat().name().equals(zutatInput.getText())) return;

                data.Zutat newZutat = data.Zutat.get(zutatInput.getText());
                newZutat.setEinheit(zutat.getZutat().getEinheit());
                zutat.setZutat(newZutat);
            }

            private void onNotizenInputChange() {
                zutat.setNotizen(notizenInput.getText().replace("\\n", ""));
            }

            private void onMengenInputChange() {
                try {
                    zutat.setMenge(Integer.parseInt(mengenInput.getText()));
                    mengenInput.setForeground(Color.BLACK);
                } catch (NumberFormatException e) {
                    mengenInput.setForeground(new Color(160, 0, 0));
                }
            }

            private void onEinheitInputChange(ActionEvent evt) {
                zutat.getZutat().setEinheit((Einheit) einheitInput.getSelectedItem());
            }
        }
    }

    private class Beschreibung extends JTextArea {
        {
            setRows(50);
            setLineWrap(true);
            setWrapStyleWord(true);

            setText(rezept.getBeschreibung());
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    onChange();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    onChange();
                }
            });
        }

        private void onChange() {
            rezept.setBeschreibung(getText());
        }
    }
}