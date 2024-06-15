package ui.util;

import google.fonts.GoogleFonts;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for various functions relating to ui
 */
public class UI {

    public static final Map<String, Font> ALL_FONTS = new HashMap<>();
    private static final String[] FONT_HIERARCHY = {"Anonymous Pro", "Comic Sans MS", "Arial"};

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
     * Creates a line that contains the specified components.
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
     * Can be called to invoke the static constructors of this class
     */
    public static void initialise() {

    }
}