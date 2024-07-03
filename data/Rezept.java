package data;

import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;

import java.util.*;

/**
 * Represents entire recipes
 */
public class Rezept {
    private String name;
    /**
     * The categories with which this recipe will be associated
     */
    private final Set<Kategorie> kategorien = new HashSet<>();
    /**
     * A short (or lengthy) introduction to the recipe
     */
    private String beschreibung;

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

    public void removeKategorie(Kategorie c) {
        kategorien.remove(c);
    }

    /**
     * Finds and add up all the ingredients that each step of the recipe needs
     */
    public Collection<ZutatInfo> getZutaten() {
        return List.copyOf(zutaten);
    }


    public JSONObject toJSON() {
        return JSON.object(
                "name", JSON.string(getName()),
                "kategorien", JSON.list(getKategorien(), c -> JSON.string(c.getName())),
                "beschreibung", JSON.string(getBeschreibung()),
                "zutaten", JSON.list(getZutaten(), ZutatInfo::toJSON)
        );
    }

    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setName(obj.get("name").string());

        for (var c : obj.get("kategorien").list())
            addKategorie(Kategorie.get(c.string()));

        //setIntroduction(obj.get("beschreibung").string());


    }
}