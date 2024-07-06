package data;

import files.Speicher;
import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Representiert die einzelnen Rezepte
 */
public class Rezept implements Comparable<Rezept> {
    private final static PropertyChangeSupport pcs = new PropertyChangeSupport(new Object());
    private final static Set<Rezept> ALLE_REZEPTE = new HashSet<>();

    static {
        Speicher.ladeAlle().forEach(ALLE_REZEPTE::add);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (var r : getAlleRezepte()) {
                try {
                    r.speicher();
                } catch (RezeptFormat e) {
                    System.err.println("Rezept war falsch formatiert");
                }
            }
        }));
    }

    {
        ALLE_REZEPTE.add(this);
    }

    private String name = "";
    /**
     * Die Kategorien
     */
    private final Set<Kategorie> kategorien = new HashSet<>();
    /**
     * Die Beschreibung von dem Rezept
     */
    private String beschreibung = "";

    private final ArrayList<ZutatInfo> zutaten = new ArrayList<>();

    private boolean istGeloscht;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        for (Rezept r : ALLE_REZEPTE) {
            if (r == this) continue;

            if (r.getName().equals(name))
                throw new IllegalArgumentException("Es gibt bereits ein Rezept mit diesem Namen (" + name + ")");
        }
        Speicher.loschen(this);
        this.name = name;
        try {
            speicher();
        } catch (RezeptFormat e) {
            //silent fail
        }
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Set<Kategorie> getKategorien() {
        return Collections.unmodifiableSet(kategorien);
    }

    public void addKategorie(Kategorie c) {
        kategorien.add(c);
    }

    /**
     * @return Ob dieses Rezept tatsächlich zu dieser Kategorie gehörte
     */
    public boolean removeKategorie(Kategorie c) {
        return kategorien.remove(c);
    }


    public void addZutat(ZutatInfo z) {
        zutaten.add(z);
    }

    /**
     * @return Ob die Zutat tatsächlich existierte (also in der Liste aufgeführt war)
     */
    public boolean removeZutat(ZutatInfo z) {
        return zutaten.remove(z);
    }

    /**
     * Verändert die Ordnung, in der die Zutaten aufgeführt werden
     * */
    public void zutatMoveUp(ZutatInfo z) {
        if (!zutaten.contains(z)) throw new IllegalArgumentException("Die Zutat ist gar nicht in dem Rezept enthalten");

        int index = zutaten.indexOf(z);
        if (index == 0) return;
        zutaten.remove(z);
        zutaten.add(index - 1, z);
    }


    /**
     * Verändert die Ordnung, in der die Zutaten aufgeführt werden
     * */
    public void zutatMoveDown(ZutatInfo z) {
        if (!zutaten.contains(z)) throw new IllegalArgumentException("Die Zutat ist gar nicht in dem Rezept enthalten");

        int index = zutaten.indexOf(z);
        if (index == zutaten.size() - 1) return;
        zutaten.remove(z);
        zutaten.add(index + 1, z);
    }

    public Collection<ZutatInfo> getZutaten() {
        return List.copyOf(zutaten);
    }

    public void speicher() throws RezeptFormat {
        if (!istValide()) throw new RezeptFormat();
        if (istGeloscht()) return;
        Speicher.speicher(this);
    }

    /**
     * Konvertiert diese Instanz zu der JSON-Representation
     */
    public JSONObject toJSON() {
        return JSON.object(
                "name", JSON.string(getName()),
                "kategorien", JSON.list(getKategorien(), c -> JSON.string(c.getName())),
                "beschreibung", JSON.string(getBeschreibung()),
                "zutaten", JSON.list(getZutaten(), ZutatInfo::toJSON)
        );
    }


    /**
     * Lädt die Daten, die in der JSON-Representation gespeichert sind, in dieses Rezept-Objekt
     */
    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setName(obj.get("name").string());

        for (var c : obj.get("kategorien").list())
            addKategorie(Kategorie.get(c.string()));

        setBeschreibung(obj.get("beschreibung").string());

        for (var z : obj.get("zutaten").list()) {
            var zutat = new ZutatInfo();
            zutat.loadJSON(z);
            addZutat(zutat);
        }
    }

    /**
     * Gibt zurück, ob das Rezept in der Form, in der es jetzt ist gespeichert werden soll
     */
    public boolean istValide() {
        return !name.isBlank();
    }

    public void loschen() {
        Speicher.loschen(this);
        ALLE_REZEPTE.remove(this);
        istGeloscht = true;
        pcs.firePropertyChange("Rezepte", this, null);
    }

    public boolean istGeloscht() {
        return istGeloscht;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Collection<Rezept> getAlleRezepte() {
        return List.copyOf(ALLE_REZEPTE);
    }

    public static class RezeptFormat extends Exception {
    }

    @Override
    public int compareTo(Rezept o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Rezept r && getName().equals(r.getName());
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public static void addPropertyListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public static void removePropertyListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
}