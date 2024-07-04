package data;

import files.Speicher;
import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;

import java.util.*;

/**
 * Represents entire recipes
 */
public class Rezept {
    private final static List<Rezept> ALLE_REZEPTE = new ArrayList<>();

    static {
        ALLE_REZEPTE.addAll(List.of(Speicher.ladeAlle()));

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            for (var r : getAlleRezepte()) {
                r.speicher();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Collection<ZutatInfo> getZutaten() {
        return List.copyOf(zutaten);
    }

    public void speicher() {
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

    public static Collection<Rezept> getAlleRezepte() {
        return List.copyOf(ALLE_REZEPTE);
    }
}