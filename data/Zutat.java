package data;

import java.util.HashMap;
import java.util.Map;

/**
 * Speichert Zutaten und die dazugehörigen Namen, es sollte nur eine Instanz pro Name geben, aber während dem editieren dürfen auch mehrere existieren
 */
public record Zutat(String name, Einheit einheit) {
    private final static Map<String, Zutat> ALL_INGREDIENTS = new HashMap<>();

    /**
     * Gibt die EINE Zutat Instanz mit dem Namen zurück
     */
    public Zutat intern() {
        return get(name);
    }

    public static Zutat get(String name) {
        if (!ALL_INGREDIENTS.containsKey(name)) {
            ALL_INGREDIENTS.put(name, new Zutat(name, Einheit.GRAMM));
        }
        return ALL_INGREDIENTS.get(name);
    }
}