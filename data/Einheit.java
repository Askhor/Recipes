package data;

/**
 * Die Einheit für eine Zutat: Volumen, Masse oder Stückzahl
 */
public enum Einheit {
    MILLILITER("ml", "MILLILITER"), GRAMM("g", "GRAMM"), STUECK("stück", "STUECK");
    public final String name;
    public final String canonicalName;

    Einheit(String name, String canonicalName) {
        this.name = name;
        this.canonicalName = canonicalName;
    }


    @Override
    public String toString() {
        return name;
    }
}