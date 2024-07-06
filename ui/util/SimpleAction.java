package ui.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Eine einfache Klasse, die Action implementiert und einfacher zu verwenden ist
 */
public class SimpleAction extends AbstractAction {
    private final Consumer<ActionEvent> action;

    public SimpleAction(String text, Icon icon, Consumer<ActionEvent> action) {
        super(text, icon);
        this.action = action;
    }

    public SimpleAction(String text, Consumer<ActionEvent> action) {
        this(text, null, action);
    }

    public SimpleAction(String text, Runnable action) {
        this(text, e -> action.run());
    }

    public SimpleAction(Icon icon, Runnable action) {
        this("", icon, e -> action.run());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.accept(e);
    }
}