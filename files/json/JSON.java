package files.json;

import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Diese Klasse ist dazu da, das JSON-Format zu lesen und zu schreiben
 * <a href="https://en.wikipedia.org/wiki/JSON">Wikipedia - JSON</a>
 * */
public class JSON {
    private static final Pattern regex = Pattern.compile("[\\sa-zA-Z\\d.,\\[\\]{}():\"#<>\\-+_!&/?ßöäüÖÜÄ\\\\;=%@*|~'•^$`´·\u200C…]*");

    public static boolean basicValidityCheck(String json) {
        return regex.matcher(json).matches();
    }

    /**
     * @return Das JSON-Objekt für eine Zahl
     * */
    public static JSONObject num(int num) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                out.print(Integer.toString(num));
            }

            @Override
            protected boolean isMultiline() {
                return false;
            }
        };
    }


    /**
     * @return Das JSON-Objekt für eine Zahl
     * */
    public static JSONObject num(double num) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                out.print(Double.toString(num));
            }

            @Override
            protected boolean isMultiline() {
                return false;
            }
        };
    }


    /**
     * @return Das JSON-Objekt für einen String
     * */
    public static JSONObject string(String string) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                out.print('"');
                out.print(string
                        .replace("\\", "\\\\")
                        .replace("\n", "\\n")
                        .replace("\"", "\\\"")
                );
                out.print('"');
            }

            @Override
            protected boolean isMultiline() {
                return false;
            }
        };
    }


    /**
     * @return Das JSON-Objekt für eine Liste
     * */
    public static JSONObject list(JSONObject... values) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                var lines = isMultiline();

                out.print('[');
                if (values.length > 0) {
                    if (lines) {
                        out.indent();
                        for (var v : values) {
                            out.newLine();
                            out.print(v);
                            out.print(',');
                        }
                        out.deleteLast();
                        out.undent();
                        out.newLine();
                    } else {
                        for (var v : values) {
                            out.print(v);
                            out.print(',');
                            out.space();
                        }
                        out.deleteLast(2);
                    }
                }
                out.print(']');
            }

            @Override
            protected boolean isMultiline() {
                for (var o : values) {
                    if (o.isMultiline()) return true;
                }
                return false;
            }
        };
    }


    /**
     * @return Das JSON-Objekt für eine Liste
     * */
    public static <T> JSONObject list(Collection<T> values, Function<T, JSONObject> transform) {
        return list(values.stream().map(transform).toArray(JSONObject[]::new));
    }


    /**
     * @return Das JSON-Objekt für ein Objekt
     * */
    public static JSONObject object(Object... values) {
        if ((values.length & 1) == 1) {
            throw new IllegalArgumentException("Illegal arguments formatting, JSON.object expects an array that alternates between the name and value");
        }
        for (int i = 0; i < values.length; i += 2) {
            if (!(values[i] instanceof String))
                throw new IllegalArgumentException("Illegal arguments formatting, JSON.object expects an array that alternates between the name and value. String was expected at " + i + ", but " + values[i].getClass() + " was found");
            if (!(values[i + 1] instanceof JSONObject))
                throw new IllegalArgumentException("Illegal arguments formatting, JSON.object expects an array that alternates between the name and value. JSONObject was expected at " + (i + 1) + ", but " + values[i + 1].getClass() + " was found");

        }

        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                out.print('{');
                if (values.length > 0) {
                    out.indent();
                    for (int i = 0; i < values.length; i += 2) {
                        out.newLine();
                        out.print('"');
                        out.print((String) values[i]);
                        out.print('"');
                        out.print(':');
                        out.space();
                        out.print((JSONObject) values[i + 1]);
                        out.print(',');
                    }
                    out.deleteLast();
                    out.undent();
                    out.newLine();

                }
                out.print('}');
            }

            @Override
            protected boolean isMultiline() {
                return values.length != 0;
            }
        };
    }


    /**
     * @return Das JSON-Objekt für einen Boolean
     * */
    public static JSONObject bool(boolean value) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                out.print(value ? "true" : "false");
            }

            @Override
            protected boolean isMultiline() {
                return false;
            }
        };

    }


    /**
     * Versucht den String als JSON zu lesen
     * */
    public static JSONValue parse(String string) throws JSONFormatException {
        if (!basicValidityCheck(string)) {
            var problemCharacters = new StringBuilder();

            for (var chars : string.codePoints().distinct().mapToObj(Character::toChars).toList()) {
                for (var c : chars) {
                    if (!regex.matcher("" + c).matches())
                        problemCharacters.append(c);
                }
            }
            throw new JSONFormatException("JSON is ill-formatted; It contains the following illegal characters (" + problemCharacters + ")");
        }
        var stream = new JSONValue.ParseStream(string);
        return JSONValue.read(stream);
    }
}