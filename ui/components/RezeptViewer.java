package ui.components;

import data.Rezept;
import data.ZutatInfo;
import ui.Fenster;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;
import java.awt.*;

/**
 * Zeigt tatsächlich nur das Rezept an, ohne es verändern zu können
 */
public class RezeptViewer extends Content {
    private final Rezept rezept;

    public RezeptViewer(Rezept rezept) {
        this.rezept = rezept;

        setLayout(new BorderLayout());
        add(menu = new SideMenu(true, true, true, true, true, true, false), BorderLayout.WEST);
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

        main.add(UI.createLine(Box.createHorizontalStrut(20), Box.createHorizontalStrut(7), titel));

        main.add(Box.createVerticalStrut(10));

        main.add(UI.createLine(kategorien));

        main.add(Box.createVerticalStrut(20));

        main.add(UI.createLine(zutaten));

        main.add(Box.createVerticalStrut(10));

        main.add(UI.createLine(Box.createHorizontalStrut(40), beschreibung));

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


    private class Titel extends JTextField {
        {
            setText(rezept.getName());
            setEditable(false);
            setFocusable(false);
        }
    }

    private class Kategorien extends JPanel {

        {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));


            for (data.Kategorie k : rezept.getKategorien()) {
                add(new Kategorien.Kategorie(k, -1));
            }

            add(Box.createHorizontalStrut(10));

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
            }
        }

        private class Kategorie extends Kategorien.Element {

            public Kategorie(data.Kategorie k, int index) {
                super(new SimpleAction(k.getName(), () -> {
                }), Color.DARK_GRAY);

                Component spacing = Box.createHorizontalStrut(10);
                if (index == -1) {
                    Kategorien.this.add(spacing);
                } else {
                    Kategorien.this.add(spacing, index);
                }
            }
        }
    }

    private class Zutaten extends JPanel {
        private final JPanel list = new JPanel();
        private final JPanel controls = new JPanel();
        private final JSpinner portionen = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));

        {
            setLayout(new BorderLayout());

            add(list, BorderLayout.CENTER);
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            for (ZutatInfo z : rezept.getZutaten()) {
                list.add(new Zutat(z));
            }

            add(controls, BorderLayout.SOUTH);
            controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
            controls.add(Box.createHorizontalStrut(20));
            controls.add(new JLabel("Anzahl an Portionen: "));
            controls.add(portionen);
            controls.add(Box.createGlue());
            portionen.addChangeListener(changeEvent -> onPortionenChange());
            portionen.setMaximumSize(new Dimension(20, 1000));
        }

        private void onPortionenChange() {
            int anzahlPortionen = ((SpinnerNumberModel) portionen.getModel()).getNumber().intValue();

            for (Component c : list.getComponents()) {
                if (c instanceof Zutat z) {
                    z.updatePortionenMenge(anzahlPortionen);
                }
            }
        }

        private static class Zutat extends JPanel {

            private final ZutatInfo zutat;
            private final JLabel mengenInput = new JLabel();

            public Zutat(ZutatInfo zutat) {
                this.zutat = zutat;

                mengenInput.setText("" + zutat.getMenge());
                JLabel einheitInput = new JLabel();
                einheitInput.setText(zutat.getZutat().getEinheit().name);
                JLabel zutatInput = new JLabel();
                zutatInput.setText(zutat.getZutat().name());
                JLabel notizenInput = new JLabel();
                notizenInput.setText(zutat.getNotizen());


                mengenInput.setHorizontalAlignment(JTextField.RIGHT);

                setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

                add(Box.createHorizontalStrut(20));

                add(mengenInput);
                add(Box.createHorizontalStrut(10));
                add(einheitInput);
                add(Box.createHorizontalStrut(10));
                add(zutatInput);
                add(Box.createHorizontalStrut(40));
                add(notizenInput);

                add(Box.createHorizontalGlue());
            }

            public void updatePortionenMenge(int portionen) {
                mengenInput.setText("" + portionen * zutat.getMenge());
            }
        }
    }

    private class Beschreibung extends JTextArea {
        public Beschreibung() {
            setEditable(false);
            setFocusable(false);
            setRows(50);
            setLineWrap(true);
            setWrapStyleWord(true);

            setText(rezept.getBeschreibung());
        }
    }
}