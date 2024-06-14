package files.json;

public interface LineStream {
    void newLine();

    void indent();

    void undent();

    void space();

    void deleteLast(int amount);

    default void deleteLast() {
        deleteLast(1);
    }

    void print(char c);

    void print(String string);

    default void print(JSONObject o) {
        o.print(this);
    }

    class ToString implements LineStream {
        private final String space;
        private final String newLine;
        private final String indentString;
        private int indent = 0;
        private final StringBuilder builder = new StringBuilder();

        public ToString(String space, String newLine, String indent) {
            this.space = space;
            this.newLine = newLine;
            indentString = indent;
        }

        @Override
        public String toString() {
            return builder.toString();
        }

        @Override
        public void newLine() {
            builder.append(newLine);
            for (int i = 0; i < indent; i++) {
                builder.append(indentString);
            }
        }

        @Override
        public void indent() {
            indent++;
        }

        @Override
        public void undent() {
            if (indent == 0)
                throw new IllegalStateException("Negative indent is not possible");
            indent--;
        }

        @Override
        public void space() {
            builder.append(space);
        }

        @Override
        public void deleteLast(int amount) {
            int l = builder.length();
            builder.delete(l - amount, l);
        }

        @Override
        public void print(char c) {
            builder.append(c);
        }

        @Override
        public void print(String string) {
            builder.append(string);
        }
    }
}