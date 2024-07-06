package ui.components;

import data.Rezept;
import data.Tuple2;
import data.Zutat;
import data.ZutatInfo;
import data.html.HtmlConverter;
import ui.Fenster;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;

/**
 * Alle Funktionalitäten zur Einkaufsliste
 */
public class Einkaufsliste extends Content {
    private static Einkaufsliste INSTANCE = null;


    /**
     * Gibt die Einkaufslisteninstanz zurück oder kreiert eine neue, falls keine in einem Fenster offen ist
     */
    public static Einkaufsliste instance() {
        if (INSTANCE != null) return INSTANCE;

        INSTANCE = new Einkaufsliste();

        Fenster f = new Fenster(INSTANCE);
        f.setResizable(false);
        f.setLocation(f.getToolkit().getScreenSize().width - f.getWidth() - 10, 50);

        INSTANCE.onZutatenChange();
        return INSTANCE;
    }

    private final Map<Zutat, ZutatE> zutaten = new HashMap<>();

    private final JPanel zutatenPanel = new JPanel();
    private final JPanel options = new JPanel();

    private Einkaufsliste() {
        setLayout(new BorderLayout());

        add(Box.createVerticalStrut(400), BorderLayout.WEST); // wooow, das funktioniert!!

        add(zutatenPanel, BorderLayout.CENTER);
        zutatenPanel.setLayout(new BoxLayout(zutatenPanel, BoxLayout.Y_AXIS));

        add(options, BorderLayout.SOUTH);
        options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
        options.add(Box.createHorizontalStrut(10));
        options.add(new JButton(new SimpleAction("Text Export", this::textExport)));
        options.add(Box.createHorizontalStrut(20));
        options.add(new JButton(new SimpleAction("HTML Export", this::htmlExport)));
        options.add(Box.createHorizontalStrut(10));
    }

    /**
     * Fügt die Zutaten aus dem Rezept portionen-Mal zur Einkaufsliste hinzu
     */
    public void addRezept(Rezept r, int portionen) {
        if (portionen == 0) return;
        for (ZutatInfo z : r.getZutaten()) {
            int menge = z.getMenge();
            if (z.getMenge() == 0) continue;

            Zutat zutat = z.getZutat();
            if (zutaten.containsKey(zutat)) {
                zutaten.get(zutat).menge += menge * portionen;
            } else {
                zutaten.put(zutat, new ZutatE(menge * portionen, zutat));
            }
        }
        onZutatenChange();
    }

    private void onZutatenChange() {
        zutatenPanel.removeAll();

        for (Zutat zutat : zutaten.keySet().stream().sorted(Comparator.comparing(Zutat::name)).toList()) {
            ZutatE e = zutaten.get(zutat);
            zutatenPanel.add(new ZutatView(e));
        }

        SwingUtilities.windowForComponent(this).pack();
        repaint();
    }

    private void htmlExport() {
        try {
            Path file = Files.createTempFile(null, ".html");
            Files.writeString(
                    file,
                    HtmlConverter.convertEinkaufsliste(
                            zutaten.values().stream()
                                    .sorted(Comparator.comparing(z -> z.zutat.name()))
                                    .map(
                                            z -> new Tuple2<>(
                                                    z.menge + " " + z.zutat.getEinheit().name + " " + z.zutat.name(),
                                                    z.checkedOff
                                            )
                                    )
                                    .toArray(Tuple2[]::new)
                    )
            );
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file.toFile());
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Die HTML-Datei wurde gespeichert unter " + file.toAbsolutePath());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), "Die Einkaufsliste konnte nicht als HTML-datei exportiert werden");
        }
    }

    /**
     * Plaintext export der Einkaufsliste
     */
    private void textExport() {
        StringBuilder text = new StringBuilder();
        for (ZutatE z : zutaten.values().stream().sorted(Comparator.comparing(z -> z.zutat.name())).toList()) {
            text.append(z.menge);
            text.append(" ");
            text.append(z.zutat.getEinheit().name);
            text.append(" ");
            text.append(z.zutat.name());
            text.append('\n');
        }
        UI.copyToClipboard(text.toString());
    }

    @Override
    public void close() {
        INSTANCE = null;
    }

    @Override
    public String getTitle() {
        return "Einkaufsliste";
    }

    /**
     * Die Implementierung für einen Button, der ein Rezept zur Einkaufsliste hinzufügt
     * */
    public static SideMenu.CustomButton zurEinkaufslisteHinzufugen(IntSupplier portionen) {
        return new SideMenu.CustomButton("Einkaufsliste", r -> {
            if (r == null) {
                JOptionPane.showMessageDialog(null, "Wähle ein Rezept aus, um es zur Einkaufsliste hinzuzufügen");
                return;
            }
            instance().addRezept(r, portionen.getAsInt());
        });
    }


    /**
     * <i><b>E</b></i>xtra information über die Zutat
     */
    private class ZutatView extends JPanel {
        private final ZutatE zutat;
        private final String rawText;
        private final JLabel text;

        public ZutatView(ZutatE zutat) {
            this.zutat = zutat;

            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener(e -> onChecking(checkBox.isSelected()));
            add(checkBox);

            rawText = zutat.menge + " " + zutat.zutat.getEinheit().name + " " + zutat.zutat.name();
            add(text = new JLabel());
            text.setText("<html>" + rawText + "</html>");
            add(Box.createHorizontalGlue());

            JPopupMenu contextMenu = new JPopupMenu();
            setComponentPopupMenu(contextMenu);
            contextMenu.add(new SimpleAction("Entfernen", () -> {
                zutaten.remove(zutat.zutat);
                onZutatenChange();
            }));
        }

        /**
         * Wird aufgerufen, wenn die CheckBox geklickt wird
         */
        private void onChecking(boolean check) {
            text.setForeground(check ? new Color(0x838383) : new Color(0));
            if (check) {
                text.setText("<html><s>" + rawText + "</s></html>");
            } else {
                text.setText("<html>" + rawText + "</html>");
            }
        }
    }

    private static final class ZutatE {

        public int menge;
        public final Zutat zutat;

        public boolean checkedOff;

        private ZutatE(int menge, Zutat zutat) {
            this.menge = menge;
            this.zutat = zutat;
        }
    }
}