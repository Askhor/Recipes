package data;

public enum Einheit {
    MILLILITER("ml"), GRAMM("g"), STUECK("stück");
    public final String name;

    Einheit(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}