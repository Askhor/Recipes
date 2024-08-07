package files.json;

import files.LineStream;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Diese Klasse ist dazu da, das JSON-Format zu lesen und zu schreiben
 * <a href="https://en.wikipedia.org/wiki/JSON">Wikipedia - JSON</a>
 */
public class JSON {
    private static final Pattern regex = Pattern.compile("[\\sa-zA-Z\\d.,\\[\\]{}():\"#<>\\-+_!&/?ßöäüÖÜÄ\\\\;=%@*|~'•^$`´·\u200C…]*");

    public static boolean basicValidityCheck(String json) {
        return regex.matcher(json).matches();
    }

    /**
     * @return Das JSON-Objekt für eine Zahl
     */
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
     */
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
     */
    public static JSONObject string(String string) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                if (string == null) {
                    out.print("null");
                    return;
                }

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
     */
    public static JSONObject list(JSONObject... values) {
        return new JSONObject() {
            @Override
            protected void print(LineStream out) {
                if (values == null) {
                    out.print("null");
                    return;
                }

                var lines = isMultiline();

                out.print('[');
                if (values.length > 0) {
                    if (lines) {
                        out.indent();
                        for (var v : values) {
                            out.newLine();
                            v.print(out);
                            out.print(',');
                        }
                        out.deleteLast();
                        out.undent();
                        out.newLine();
                    } else {
                        for (var v : values) {
                            v.print(out);
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
     */
    public static <T> JSONObject list(Collection<T> values, Function<T, JSONObject> transform) {
        if (values == null)
            return list((JSONObject) null);
        return list(values.stream().map(transform).toArray(JSONObject[]::new));
    }


    /**
     * @return Das JSON-Objekt für ein Objekt
     */
    public static JSONObject object(Object... values) {
        if (values == null) {
            return new JSONObject() {
                @Override
                protected void print(LineStream out) {
                    out.print("null");
                }

                @Override
                protected boolean isMultiline() {
                    return false;
                }
            };
        }
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
                        ((JSONObject) values[i + 1]).print(out);
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
     * @return Das JSON-Objekt für ein Objekt
     */
    public static <T> JSONObject object(Map<String, T> map, Function<T, JSONObject> transform) {
        if (map == null)
            return object((Object) null);

        var values = new Object[map.size() * 2];

        int i = 0;

        for (var key : map.keySet()) {
            values[i] = key;
            values[i + 1] = transform.apply(map.get(key));
            i += 2;
        }

        return object(values);
    }

    /**
     * @return Das JSON-Objekt für ein Objekt
     */
    public static JSONObject object(Map<String, JSONObject> map) {
        return object(map, o -> o);
    }


    /**
     * @return Das JSON-Objekt für einen Boolean
     */
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
     */
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