package ui.components;

import data.Kategorie;
import data.Rezept;
import ui.Fenster;
import ui.util.UI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Das Hauptmenü
 */
public class MainMenu extends Content {
    //		Rezept-Liste:
    private final DefaultListModel<Rezept> rezeptListModel = new DefaultListModel<>();
    private final JList<Rezept> rezeptList = new JList<>(rezeptListModel);
    private final PropertyChangeListener rezepteListener = e -> {
        updateRezeptList();
    };

    private Predicate<Rezept> filter = r -> true;

    public MainMenu() {
        setLayout(new BorderLayout());

        add(menu = new SideMenu(
                true,
                true,
                true,
                false,
                true,
                true,
                false,
                Einkaufsliste.zurEinkaufslisteHinzufugen(() -> 1),
                Wochenplan.zumWochenplanHinzufugen(),
                new SideMenu.CustomButton("Font",  r -> FontEinstellungen.offnen())
        ), BorderLayout.WEST);

        add(new Suche(), BorderLayout.NORTH);

        add(new JScrollPane(rezeptList), BorderLayout.CENTER);
        rezeptList.addListSelectionListener(e -> {
            Rezept r = rezeptList.getSelectedValue();
            menu.setSelectedRezept(r);
        });
        rezeptList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1) {
                    Window w = SwingUtilities.windowForComponent(MainMenu.this);
                    if (!(w instanceof Fenster f)) {
                        System.err.println("MainMenu sollte immer innerhalb eines Fensters verwendet werden");
                        return;
                    }
                    Rezept selection = rezeptList.getSelectedValue();
                    if (selection == null) return;

                    f.setContent(new RezeptViewer(selection));
                }
            }
        });

        updateRezeptList();

        Rezept.addPropertyListener(rezepteListener);
    }

    private void updateRezeptList() {
        rezeptListModel.clear();
        var rezepte = Rezept.getAlleRezepte();
        rezepte = rezepte.stream().filter(filter).sorted().toList();

        for (Rezept rezept : rezepte) {
            rezeptListModel.addElement(rezept);
        }
    }

    @Override
    public void close() {
        Rezept.removePropertyListener(rezepteListener);
    }

    @Override
    public String getTitle() {
        return "Menü (mhmmm, lecker!)";
    }

    class Suche extends JPanel {
        private static final Pattern suchPattern = Pattern.compile("\\s*([\\w#]+)?(\\s+[\\w#]+)*\\s*");
        private final JTextField textInput = new JTextField();
        private final JCheckBox regex = new JCheckBox("Regex");

        public Suche() {
            setLayout(new BorderLayout());
            add(new JLabel("Suche: "), BorderLayout.WEST);
            JLabel eastern = new JLabel("#<Kategorie>, um nach Kategorien zu suchen");
            eastern.setFont(UI.getFont(14));
            add(eastern, BorderLayout.EAST);
            add(textInput, BorderLayout.CENTER);

            textInput.setInheritsPopupMenu(true);

            JPopupMenu contextMenu = new JPopupMenu();
            setComponentPopupMenu(contextMenu);
            contextMenu.add(regex);

            regex.addActionListener(e -> onChange());
            textInput.getDocument().addDocumentListener(new DocumentListener() {
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
            if (regex.isSelected()) {
                try {
                    Pattern pattern = Pattern.compile(textInput.getText());
                    filter = r -> pattern.matcher(r.getName()).matches();
                    textInput.setForeground(Color.BLACK);
                    updateRezeptList();
                } catch (PatternSyntaxException e) {
                    textInput.setForeground(new Color(160, 0, 0));
                }
            } else {
                if (!suchPattern.matcher(textInput.getText()).matches()) {
                    textInput.setForeground(new Color(160, 0, 0, 0));
                    return;
                }
                textInput.setForeground(Color.BLACK);

                var kategorien = new HashSet<String>();
                var literals = new HashSet<String>();

                for (String token : textInput.getText().split("\\s+")) {
                    if (token.startsWith("#")) {
                        kategorien.add(token.substring(1).toLowerCase());
                    } else {
                        literals.add(token.toLowerCase());
                    }
                }

                filter = r -> {
                    int hits = 0;

                    for (Kategorie k : r.getKategorien()) {
                        if (kategorien.contains(k.getName().toLowerCase()))
                            hits++;
                    }

                    if (hits < kategorien.size()) return false;

                    hits = 0;

                    String name = r.getName().toLowerCase();

                    for (String l : literals) {
                        if (name.contains(l)) hits++;
                    }

                    return hits >= literals.size();
                };

                updateRezeptList();
            }
        }
    }
}