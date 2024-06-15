package files.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class JSONValue {
    private static final Pattern INTEGER = Pattern.compile("[-+]?\\d*");

    public abstract String type();

    public boolean bool() throws JSONFormatException {
        throw new JSONFormatException("A boolean was expected, but a " + type() + " was found");
    }
    public int num() throws JSONFormatException {
        throw new JSONFormatException("A number was expected, but a " + type() + " was found");
    }

    public double real() throws JSONFormatException {
        throw new JSONFormatException("A number was expected, but a " + type() + " was found");
    }

    public String string() throws JSONFormatException {
        throw new JSONFormatException("A string was expected, but a " + type() + " was found");
    }

    public List<JSONValue> list() throws JSONFormatException {
        throw new JSONFormatException("A list was expected, but a " + type() + " was found");
    }

    public Map<String, JSONValue> object() throws JSONFormatException {
        throw new JSONFormatException("An object was expected, but a " + type() + " was found");
    }

    @Override
    public String toString() {
        try {
            return (switch (type()) {
                case "List" -> list();
                case "Object" -> object();
                case "String" -> string();
                case "Number" -> num();
                case "Real Number" -> real();
                case "Bool" -> bool();
                default -> "Error";
            }).toString();
        } catch (JSONFormatException e) {
            e.printStackTrace();
            return "Double Error";
        }
    }

    protected static JSONValue read(ParseStream in) throws JSONFormatException {
        in.skipWhitespace();
        char c = in.c();
        return switch (c) {
            case '{' -> readObject(in);
            case '[' -> readList(in);
            case '"' -> readString(in);
            case 'n' -> {
                in.expect('n');
                in.expect('u');
                in.expect('l');
                in.expect('l');
                yield null;
            }
            case 't','f' -> readBool(in);
            default -> {
                if (!Character.isDigit(c) && c != '.' && c != '-' && c != '+')
                    throw new JSONFormatException("JSON was incorrectly formatted. The problematic characters are " + in.readNext(10));
                yield readNumber(in);
            }
        };
    }

    protected static JSONValue readBool(ParseStream in) throws JSONFormatException {
        var value = in.c() == 't';
        if (value) {
            in.expect('t');
            in.expect('r');
            in.expect('u');
            in.expect('e');
        } else {
            in.expect('f');
            in.expect('a');
            in.expect('l');
            in.expect('s');
            in.expect('e');
        }
        return new JSONValue() {
            @Override
            public String type() {
                return "Bool";
            }

            @Override
            public boolean bool() {
                return value;
            }
        };
    }

    protected static JSONValue readObject(ParseStream in) throws JSONFormatException {
        var values = new HashMap<String, JSONValue>();
        in.move(1);
        loop:
        while (true) {
            in.skipWhitespace();
            if (in.c() == '}') break;
            in.expect('"');
            var name = in.readUntil('"');
            in.skipWhitespace();
            in.expect(':');
            in.skipWhitespace();
            values.put(name, read(in));

            in.skipWhitespace();

            switch (in.c()) {
                case ',' -> in.move(1);
                case '}' -> {
                    break loop;
                }
                default ->
                        throw new JSONFormatException("Expected (}) or (,) , but " + in.c() + " was found. The problematic characters are " + in.readNext(10));
            }
        }
        in.move(1);

        return new JSONValue() {
            @Override
            public String type() {
                return "Object";
            }

            @Override
            public Map<String, JSONValue> object() {
                return values;
            }
        };
    }

    protected static JSONValue readList(ParseStream in) throws JSONFormatException {
        var values = new ArrayList<JSONValue>();
        in.move(1);


        loop:
        while (true) {
            in.skipWhitespace();
            if (in.c() == ']') break;
            values.add(read(in));
            in.skipWhitespace();

            switch (in.c()) {
                case ',' -> in.move(1);
                case ']' -> {
                    break loop;
                }
                default -> throw new JSONFormatException("Expected (]) or (,), but " + in.c() + " was found");
            }
        }
        in.move(1);

        return new JSONValue() {
            @Override
            public String type() {
                return "List";
            }

            @Override
            public List<JSONValue> list() {
                return values;
            }
        };
    }

    protected static JSONValue readNumber(ParseStream in) {
        String rawNumber = in.readNumber();
        if (INTEGER.matcher(rawNumber).matches()) {
            int i = Integer.parseInt(rawNumber);
            return new JSONValue() {
                @Override
                public String type() {
                    return "Number";
                }

                @Override
                public int num() {
                    return i;
                }

                @Override
                public double real() {
                    return i;
                }
            };
        } else {
            double d = Double.parseDouble(rawNumber);
            return new JSONValue() {
                @Override
                public String type() {
                    return "Real Number";
                }

                @Override
                public double real() {
                    return d;
                }
            };
        }
    }

    protected static JSONValue readString(ParseStream in) {
        in.move(1);
        final var string = in.readUntil('\\', '"')
                .replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\n", "\n");

        return new JSONValue() {
            @Override
            public String type() {
                return "String";
            }

            @Override
            public String string() {
                return string;
            }
        };
    }

    protected static class ParseStream {
        private final String peer;
        private int offset = 0;

        public ParseStream(String input) {
            peer = input;
        }

        public void skipWhitespace() {
            while (Character.isWhitespace(c())) {
                offset++;
            }
        }

        public void move(int amount) {
            offset += amount;
        }

        public char c() {
            return c(0);
        }

        public char c(int o) {
            return peer.charAt(o + offset);
        }

        public void expect(char c) throws JSONFormatException {
            if (c != c()) {
                throw new JSONFormatException("Expected was " + c + ", but " + c() + " was found");
            }
            move(1);
        }

        public String readUntil(char undelimiter, char delimiter) {
            int index = peer.indexOf(delimiter, offset);
            while (peer.charAt(index - 1) == undelimiter) {
                index = peer.indexOf(delimiter, index + 1);
            }
            var out = peer.substring(offset, index);
            offset = index + 1;
            return out;
        }

        public String readUntil(char delimiter) {
            int index = peer.indexOf(delimiter, offset);
            var out = peer.substring(offset, index);
            offset = index + 1;
            return out;
        }

        public String readNext(int amount) {
            var out = peer.substring(offset, offset + amount);
            offset += amount;
            return out;
        }

        public String readNumber() {
            int index = offset;

            while (true) {
                var c = peer.charAt(index);
                if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                    index++;
                } else break;
            }

            var out = peer.substring(offset, index);
            offset = index;
            return out;
        }
    }
}