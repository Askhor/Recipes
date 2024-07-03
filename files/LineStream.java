package files;


/**
 * Mit dieser Klasse kann man die Kreierung von mehrzeiligen Strings zwischen Klassen aufteilen
 * */
public interface LineStream {
    /**
     * Fängt eine neue Zeile mit dem jetzigen indent an
     * */
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

    /**
     * Fängt eine neue Zeile mit dem jetzigen indent an und schreib den String rein
     */
    default void line(String string) {
        newLine();
        print(string);
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

        /**
         * Kreiert eine neue LineStream Instanz mit default parametern
         * */
        public ToString() {
            this(" ", "\n", " ".repeat(4));
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