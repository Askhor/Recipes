package files.json;

public abstract class JSONObject {
    protected abstract void print(LineStream out);

    protected abstract boolean isMultiline();


    @Override
    public final String toString() {
        return toString(true);
    }

    private String toString(boolean prettyPrint) {
        String space = "", newLine = "", indent = "";
        if (prettyPrint) {
            space = " ";
            newLine = "\n";
            indent = "    ";
        }

        var out = new LineStream.ToString(space, newLine, indent);
        print(out);
        return out.toString();
    }
}