package ui.components;

import data.Rezept;
import ui.Fenster;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Der Wochenplan
 */
public class Wochenplan extends Content {

    private static final String[] WOCHEN_TAGE = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};

    private static Wochenplan INSTANCE = null;

    private final DefaultListModel<RezeptView> rezepte = new DefaultListModel<>();
    private final JList<RezeptView> rezepteComponent = new JList<>(rezepte);

    /**
     * Der Tag, an dem die Liste anfangen soll (0-6)
     */
    private int startTag;

    /**
     * Gibt die Wochenplan-Instanz zurück oder kreiert eine neue, falls keine in einem Fenster offen ist
     */
    public static Wochenplan instance() {
        if (INSTANCE != null) return INSTANCE;

        INSTANCE = new Wochenplan();

        Fenster f = new Fenster(INSTANCE);
        f.setResizable(false);
        f.setLocation(f.getToolkit().getScreenSize().width - f.getWidth() - 10, 50);

        f.pack();

        return INSTANCE;
    }

    private Wochenplan() {
        setLayout(new BorderLayout());

        add(rezepteComponent, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        add(controls, BorderLayout.WEST);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));


        controls.add(Box.createVerticalGlue());
        controls.add(new JButton(new SimpleAction("Hoch", this::hochDamit)));
        controls.add(Box.createVerticalStrut(10));
        {
            JButton delete = new JButton(new SimpleAction(UI.getIcon("/bilder/müll smol.png"), this::wegDamit));
            controls.add(delete);
            delete.setForeground(Color.RED);
        }
        controls.add(Box.createVerticalStrut(10));
        controls.add(new JButton(new SimpleAction("Runter", this::runterDamit)));
        controls.add(Box.createVerticalGlue());

        add(Box.createHorizontalStrut(400), BorderLayout.NORTH);
        add(Box.createVerticalStrut(600), BorderLayout.EAST);

        rezepteComponent.setInheritsPopupMenu(true);
        controls.setInheritsPopupMenu(true);

        JPopupMenu menu = new JPopupMenu();
        setComponentPopupMenu(menu);
        menu.add("Woche anfangen bei");
        menu.addSeparator();

        for (int i = 0; i < 7; i++) {
            int javaIsWeird = i;
            menu.add(new SimpleAction(WOCHEN_TAGE[i], () -> {
                startTag = javaIsWeird;
                int[] indices = new int[rezepte.size()];
                Arrays.setAll(indices, j -> j);
                rezepteComponent.setSelectedIndices(indices);
                rezepteComponent.clearSelection();
            }));
        }
    }

    public void addRezept(Rezept r) {
        rezepte.add(rezepte.size(), new RezeptView(this, r));
        SwingUtilities.windowForComponent(this).pack();
    }

    /**
     * Das Schöne am Programmieren ist, dass man allem selber einen Namen geben darf.
     * Es ist hoffentlich offensichtlich, was diese Methode macht, sie bewegt die Rezepte nach oben, also das selektierte
     */
    private void hochDamit() {
        RezeptView value = rezepteComponent.getSelectedValue();
        if (value == null) return;

        int index = rezepte.indexOf(value);
        System.out.println(index);
        if (index == 0) return;

        rezepte.remove(index);
        rezepte.add(index - 1, value);
        rezepteComponent.setSelectedIndex(index - 1);

        rezepteComponent.validate();
    }

    /**
     * Bewegt das selektierte Rezept nach unten
     */
    private void runterDamit() {
        RezeptView value = rezepteComponent.getSelectedValue();
        if (value == null) return;

        int index = rezepte.indexOf(value);
        System.out.println(index);
        if (index == rezepte.size() - 1) return;

        rezepte.remove(index);
        rezepte.add(index + 1, value);
        rezepteComponent.setSelectedIndex(index + 1);

        rezepteComponent.validate();
    }

    /**
     * Entfernt das selektierte Rezept
     */
    private void wegDamit() {
        int[] indices = rezepteComponent.getSelectedIndices();
        if (indices.length == 0) return;
        Arrays.sort(indices);
        for (int j = indices.length - 1; j >= 0; j--) {
            rezepte.remove(indices[j]);
        }
        rezepteComponent.setSelectedIndex(indices[0]);

        rezepteComponent.validate();
    }

    @Override
    public String getTitle() {
        return "Wochenplan";
    }

    @Override
    public void close() {
        INSTANCE = null;
    }

    public static SideMenu.CustomButton zumWochenplanHinzufugen() {
        return new SideMenu.CustomButton("Wochenplan", r -> {
            if (r == null) {
                JOptionPane.showMessageDialog(null, "Wähle ein Rezept aus, um es zum Wochenplan- hinzuzufügen");
                return;
            }
            instance().addRezept(r);
        });
    }

    private record RezeptView(Wochenplan plan, Rezept r) {
        @Override
        public String toString() {
            return
                    WOCHEN_TAGE[(plan.startTag + plan.rezepte.indexOf(this)) % 7]
                    + " "
                    + r.getName();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }
}