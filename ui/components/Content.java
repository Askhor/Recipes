package ui.components;

import javax.swing.*;
import java.io.Closeable;


/**
 * Content, das in das Hauptfenster platziert werden kann
 */
public abstract class Content extends JPanel implements Closeable {
    protected SideMenu menu;

    /**
     * Wird in dem Fenstertitel angezeigt
     */
    public abstract String getTitle();

    @Override
    public void close() {
    }
}