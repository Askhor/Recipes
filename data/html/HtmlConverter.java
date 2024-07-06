package data.html;


import data.Rezept;
import files.LineStream;

/**
 * Eine Klasse, ob die Rezepte auch als html ausgeben zu können
 */
public class HtmlConverter {
    public static String convert(Rezept rezept) {
        var out = new LineStream.ToString(" ", "\n", " ".repeat(4));

        out.line("<html>");
        out.indent();
        {
            out.line("<head>");
            out.indent();
            head(rezept, out);
            out.undent();
            out.line("</head>");
        }

        {
            out.line("<body>");
            out.indent();
            body(rezept, out);
            out.undent();
            out.line("</body>");
        }

        out.undent();
        out.line("</html>");

        return out.toString();
    }

    private static void head(Rezept rezept, LineStream out) {
        out.newLine();
        out.print("<title>");
        out.print(rezept.getName());
        out.print("</title>");

        out.line("<style>");
        out.indent();
        style(out);
        out.undent();
        out.line("</style>");
    }

    private static void style(LineStream out, String selector, String... attributes) {
        out.line(selector + " {");
        out.indent();

        for (String a : attributes) {
            out.newLine();
            out.print(a);
            out.print(';');
        }

        out.undent();
        out.line("}");
    }

    private static void style(LineStream out) {
        style(out, ".kategorie",
                "border: 2px gray solid",
                "border-radius: 20px",
                "margin: 5px",
                "padding: 4px"
        );

        style(out, ".beschreibung",
                "font-family: cursive",
                "margin-left: 20px");

        style(out, ".zutaten",
                "border: 2px gray solid",
                "margin-right: 70vw",
                "padding-top: 20px",
                "padding-bottom: 20px");
    }

    private static void body(Rezept rezept, LineStream out) {
        out.newLine();
        out.print("<h1>");
        out.print(rezept.getName());
        out.print("</h1>");

        out.line("<hr>");
        {
            out.line("<div>");
            out.indent();
            for (var k : rezept.getKategorien()) {
                out.newLine();
                out.print("<span class=\"kategorie\">");
                out.print(k.getName());
                out.print("</span>");
            }
            out.undent();
            out.line("</div>");
        }

        out.line("<br>");

        {
            out.line("<h2>Zutaten</h2>");
            out.line("<ul class=\"zutaten\">");
            out.indent();
            for (var z : rezept.getZutaten()) {
                var name = z.getZutat().name();
                var einheit = z.getZutat().getEinheit().name;
                var menge = z.getMenge();
                var notizen = z.getNotizen();

                out.line("<li>");
                out.indent();
                out.line(menge + " " + einheit + " " + name);
                if (!notizen.isBlank()) {
                    out.print(" (<i>" + notizen + "</i>)");
                }
                out.undent();
                out.line("</li>");
            }
            out.undent();
            out.line("</ul>");
        }

        out.line("<br>");

        {
            out.line("<div class=\"beschreibung\">");
            out.indent();

            for (var line : rezept.getBeschreibung().split("\n")) {
                out.newLine();
                out.print(line);
                out.print(" <br>");
            }

            out.undent();
            out.line("</div>");
        }
    }
}