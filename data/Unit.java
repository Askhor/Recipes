package data;

public enum Unit {
    ML("ml"), G("g");
    public final String name;

    Unit(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}