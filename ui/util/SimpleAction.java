package ui.util;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * A simple class to implement the functionality of Actions
 * */
public class SimpleAction extends AbstractAction {
    private final Runnable action;

    public SimpleAction(String text, Icon icon, Runnable action) {
        super(text, icon);
        this.action = action;
    }

    public SimpleAction(String text, Runnable action) {
        this(text, null, action);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.run();
    }
}