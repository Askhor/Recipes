package ui.util;

import files.Resources;
import files.Speicher;
import google.fonts.GoogleFonts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * <div>
 * Eine Klasse, die diverse methoden zum UI enthält.
 * </div>
 * <hr>
 * <div>
 * <b>HINWEIS:</b> UI.initialise() sollte in der main Methode aufgerufen werden, ansonsten könnte das UI sehr seltsam aussehen
 * </div>
 */
public class UI {
    private static Path USER_FONT_FILE = Speicher.getPath("userfont.txt");
    private static final Resources<BufferedImage> bilder = new Resources<>(s -> {
        try {
            return ImageIO.read(s);
        } catch (IOException e) {
            System.err.println("Bild konnte nicht geladen werden\n" + e.getMessage());
            return null;
        }
    });
    private static final String[] FONT_HIERARCHY = {userPreferenceFont(), Font.DIALOG};

    /**
     * A way cooler cursor that whatever you have
     * */
    public static final Cursor COOL_CURSOR = createTheCoolCursor();

    private static Font GLOBAL_FONT = findBestGlobalFont();

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            System.err.println("Error during startup. UI customisation seems to have failed");
            e.printStackTrace();
        }
    }

    static {
        setFontGlobally(getFont(20), false);
    }

    /**
     * Takes the first font from the hierarchy that is available
     */
    private static Font findBestGlobalFont() {
        for (var name : FONT_HIERARCHY) {
            if (name == null) continue;

            Font font = tryResolveToFont(name);
            if (font != null) return font;
        }

        throw new Error("None of the specified fonts are available.");
    }

    /**
     * Get a font with the specified size
     */
    public static Font getFont(float size) {
        return GLOBAL_FONT.deriveFont(size);
    }

    /**
     * Sets the font for all ui components
     */
    private static void setFontGlobally(Font font, boolean updateCurrentComponents) {
        // Thank the lord for Stackoverflow
        for (var key : UIManager.getDefaults().keySet()) {
            var value = UIManager.get(key);

            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }

        if (updateCurrentComponents) {
            for (Window w : Window.getWindows()) {
                resetFontRecursive(w);
            }
        }
    }

    private static void resetFontRecursive(Component c) {
        c.setFont(getFont(c.getFont().getSize2D()));

        if (c instanceof Container con) {
            for (Component child : con.getComponents()) {
                resetFontRecursive(child);
            }
        }
    }

    private static Font tryResolveToFont(String name) {
        Font font = Font.decode(name);

        if (font != null) return font;

        font = GoogleFonts.get(name);
        return font;
    }

    /**
     * Kreiert eine Zeile, die die spezifizierten Komponenten enthält
     */
    public static JPanel createLine(Component... components) {
        var out = new JPanel();
        out.setLayout(new BoxLayout(out, BoxLayout.X_AXIS));

        for (var c : components) {
            out.add(c);
        }
        out.add(Box.createGlue());

        return out;
    }

    /**
     * Fügt den Text dem Clipboard hinzu
     */
    public static void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(text), null
        );
    }

    /**
     * Wenn diese Methode aufgerufen wird, dann wird garantiert, dass alle static {} Blöcke in dieser Klasse aufgerufen werden
     */
    public static void initialise() {

    }


    /**
     * Gibt das Icon mit dem Ressourcennamen zurück
     */
    public static Icon getIcon(String name) {
        return new ImageIcon(getIconImage(name));
    }

    /**
     * Gibt das Bild mit dem Ressourcennamen zurück
     */
    public static Image getIconImage(String name) {
        return bilder.get(name);
    }

    /**
     * @return The font that was set by the user
     */
    private static String userPreferenceFont() {
        try {
            if (!Files.exists(USER_FONT_FILE)) {
                System.out.println("User hat keinen eigenen Font gesetzt");
                return null;
            }
            return Files.readString(USER_FONT_FILE);
        } catch (IOException e) {
            System.err.println("User-Font konnte nicht geladen werden\n" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Versucht den Applikations-Font zu den spezifizierten zu wechseln
     *
     * @return Ob der name einen validen Font spezifiziert hatte
     */
    public static boolean trySetGlobalFont(String name) {
        Font font = tryResolveToFont(name);
        if (font == null) return false;
        GLOBAL_FONT = font;

        try {
            Files.createDirectories(USER_FONT_FILE.getParent());
            Files.writeString(USER_FONT_FILE, name);
        } catch (IOException e) {
            System.err.println("User-Font konnte nicht gespeichert werden\n" + e.getLocalizedMessage());
        }

        setFontGlobally(font, true);

        return true;
    }

    private static Cursor createTheCoolCursor() {
        return Toolkit
                .getDefaultToolkit()
                .createCustomCursor(
                        getIconImage("/bilder/cursor.png"),
                        new Point(8,8),
                        "Cooler Cursor"
                );
    }
}
