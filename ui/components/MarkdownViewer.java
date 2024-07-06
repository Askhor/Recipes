package ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Diese Klasse funktioniert nicht
 * */
@Deprecated()
public class MarkdownViewer extends JPanel {
    private final JEditorPane pane = new JEditorPane();

    public MarkdownViewer() {
        this("");
    }

    public MarkdownViewer(String text) {
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);

        pane.setContentType("text/html");
        pane.setEditable(false);
        pane.setFocusable(false);

        setText(text);
    }

    public void setText(String text) {
        pane.setText(markdownToHTML(text));
    }

    private static String markdownToHTML(String string) {
        StringBuilder out = new StringBuilder("<html>");

        for (String l : string.split("[\n\r]")) {
            if (l.startsWith("#")) {
                String tag = "h1";

                if (l.startsWith("##")) {
                    tag = "h2";
                    if (l.startsWith("###")) {
                        tag = "h3";
                    }
                }

                out.append('<');
                out.append(tag);
                out.append('>');
                out.append(l.replaceFirst("#+", ""));
                out.append("</");
                out.append(tag);
                out.append('>');

                continue;
            }

            out.append("<div>");
            out.append(l);
            out.append("</div>");
            out.append("<br>");
            out.append('\n');
        }

        out.append("</html>");

        System.out.println(out);

        return out.toString();
    }
}