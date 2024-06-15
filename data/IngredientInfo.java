package data;

import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;

public class IngredientInfo {
    private Ingredient ingredient;
    private int amount;
    private String notes;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public JSONObject toJSON() {
        return JSON.object(
                "name", JSON.string(ingredient.name()),
                "amount", JSON.num(amount),
                "notes", JSON.string(notes)
        );
    }

    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setIngredient(Ingredient.get(obj.get("name").string()));
        setAmount(obj.get("amount").num());
        setNotes(obj.get("notes").string());
    }
}