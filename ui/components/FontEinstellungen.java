package ui.components;

import google.fonts.GoogleFonts;
import ui.Fenster;
import ui.util.SimpleAction;
import ui.util.UI;

import javax.swing.*;
import java.awt.*;

public class FontEinstellungen extends Content {

    private static FontEinstellungen INSTANCE = null;


    /**
     * Gibt die FontEinstellungen-Instanz zurück oder kreiert eine neue, falls keine in einem Fenster offen ist
     */
    public static FontEinstellungen instance() {
        if (INSTANCE != null) return INSTANCE;

        INSTANCE = new FontEinstellungen();

        Fenster f = new Fenster(INSTANCE);
        f.setResizable(false);
        f.pack();
        f.setLocation(f.getToolkit().getScreenSize().width - f.getWidth() - 10, 50);

        return INSTANCE;
    }


    private final JTextField input;
    private final JTextArea preview;

    private FontEinstellungen() {
        setLayout(new BorderLayout());

        input = new JTextField(UI.getFont(1).getFamily());
        add(input, BorderLayout.CENTER);
        input.addActionListener(e -> onTextInput());

        preview = new JTextArea("""
                Dorem ipsum oder so in die Richtung. Ich bin jetzt zu faul das nachzuschauen, aber du kannst gerne in den source code gehen und es ändern.
                (btw. Enter, um den Font anzuwenden)
                Mit dem G Knopf da rechts kann man einen Google API-Key setzen (für fonts) der Knopf darunter hat mehr Infos""");
        preview.setEditable(false);
        preview.setFocusable(false);
        preview.setLineWrap(true);
        preview.setWrapStyleWord(true);
        add(preview, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.EAST);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.add(new JButton(new SimpleAction(UI.getIcon("/bilder/goggle.png"), this::showKeyInput)));
        buttonPanel.add(new JButton(new SimpleAction(UI.getIcon("/bilder/hä.png"), this::showInfo)));
    }

    private void showKeyInput() {
        String input = JOptionPane.showInputDialog("Gib doch hier bitte einen validen Google-API key ein. Danke.");
        GoogleFonts.setVerySecretApiKey(input.strip());
    }

    private void showInfo() {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), """
                Also mir war mal langweilig und dann hab ich etwas mit der Google-API rumgespielt
                und hab rausgefunden, dass es eine Google-API gibt, um auf Google Fonts zuzugreifen.
                Dann hab ich etwas code dafür geschrieben und den schlussendlich in dieses Projekt integriert.
                Leider braucht man für die API einen Key, welchen ich ungern in ein mehr oder weniger
                öffentliches Projekt hard-coden will, weswegen es die Option gibt einen eigenen Key
                zu speichern.
                """);
    }

    private void onTextInput() {
        boolean success = UI.trySetGlobalFont(input.getText().strip());
        input.setForeground(success ? Color.BLACK : new Color(160, 0, 0));
        preview.setFont(UI.getFont(20));
        SwingUtilities.windowForComponent(this).pack();
    }

    @Override
    public String getTitle() {
        return "Font Einstellungen :)";
    }

    @Override
    public void close() {
        INSTANCE = null;
    }

    public static void offnen() {
        instance();
    }
}