package data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Zutat {

    private final static Map<String, Zutat> ALL_INGREDIENTS = new HashMap<>();
    public static final Zutat DEFAULT = get("Nichts");
    private final String name;
    private Einheit einheit;

    private Zutat(String name, Einheit einheit) {
        this.name = name;
        this.einheit = einheit;
    }

    public static Zutat get(String name) {
        if (!ALL_INGREDIENTS.containsKey(name.toLowerCase())) {
            ALL_INGREDIENTS.put(name.toLowerCase(), new Zutat(name, Einheit.STUECK));
        }
        return ALL_INGREDIENTS.get(name.toLowerCase());
    }

    public String name() {
        return name;
    }

    public Einheit getEinheit() {
        return einheit;
    }

    public void setEinheit(Einheit e) {
        einheit = e;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, einheit);
    }

    @Override
    public String toString() {
        return "Zutat[" +
               "name=" + name + ", " +
               "einheit=" + einheit + ']';
    }
}