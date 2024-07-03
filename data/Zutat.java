package data;

import java.util.HashMap;
import java.util.Map;

public record Zutat(String name, Einheit einheit) {
    private final static Map<String, Zutat> ALL_INGREDIENTS = new HashMap<>();

    /**
     * Gibt die Zutat Instanz für den Namen zurück
     */
    public Zutat intern() {
        return get(name);
    }

    public static Zutat get(String name) {
        if (!ALL_INGREDIENTS.containsKey(name)) {
            ALL_INGREDIENTS.put(name, new Zutat(name, Einheit.G));
        }
        return ALL_INGREDIENTS.get(name);
    }
}