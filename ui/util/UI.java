package ui.util;

import google.fonts.GoogleFonts;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
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

    public static final Map<String, Font> ALL_FONTS = new HashMap<>();
    private static final String[] FONT_HIERARCHY = {"Times New Roman", "Comic Sans MS", "Arial"};

    static {
        for (var f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
            if (f.isPlain())
                ALL_FONTS.put(f.getName(), f);
        }
    }

    private static final Font GLOBAL_FONT = findBestGlobalFont();

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
        setGlobalFont(getFont(20));
    }

    /**
     * Takes the first font from the hierarchy that is available
     */
    private static Font findBestGlobalFont() {
        for (var name : FONT_HIERARCHY) {
            Font font = ALL_FONTS.get(name);
            if (font != null) return font;
            font = GoogleFonts.get(name);
            if (font != null) return font;
        }

        throw new Error("None of the specified fonts are available.\nThe only available fonts are " + ALL_FONTS.keySet());
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
    private static void setGlobalFont(Font font) {
        // Thank the lord for Stackoverflow
        for (var key : UIManager.getDefaults().keySet()) {
            var value = UIManager.get(key);

            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
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
     * */
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
}