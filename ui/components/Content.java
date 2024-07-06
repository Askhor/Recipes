package ui.components;

import javax.swing.*;


/**
 * Content, das in das Hauptfenster platziert werden kann
 */
public abstract class Content extends JPanel {
    protected SideMenu menu;

    /**
     * Wird in dem Fenstertitel angezeigt
     */
    public abstract String getTitle();

    public void close() {
    }
}