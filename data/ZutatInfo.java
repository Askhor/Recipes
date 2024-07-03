package data;

import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;

/**
 * Speichert zusätzlich zu welcher Zutat es isst, auch wie viel davon und Notizen
 * */
public class ZutatInfo {
    private Zutat zutat;
    private int menge;
    private String notizen;

    public Zutat getZutat() {
        return zutat;
    }

    public void setZutat(Zutat zutat) {
        this.zutat = zutat;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public String getNotizen() {
        return notizen;
    }

    public void setNotizen(String notizen) {
        this.notizen = notizen;
    }

    public JSONObject toJSON() {
        return JSON.object(
                "name", JSON.string(zutat.name()),
                "amount", JSON.num(menge),
                "notes", JSON.string(notizen)
        );
    }

    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setZutat(Zutat.get(obj.get("name").string()));
        setMenge(obj.get("amount").num());
        setNotizen(obj.get("notes").string());
    }
}