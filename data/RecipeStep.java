package data;

import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;
import util.AList;

import java.util.List;

public class RecipeStep {
    private String title;
    private final AList<IngredientInfo> ingredients = new AList<>();
    private String description;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<IngredientInfo> getIngredients() {
        return ingredients.view;
    }

    public void addIngredient(IngredientInfo ingredient) {
        ingredients.add(ingredient);
    }

    public void removeIngredient(IngredientInfo ingredient) {
        ingredients.remove(ingredient);
    }

    public JSONObject toJSON() {
        return JSON.object(
                "title", JSON.string(title),
                "ingredients", JSON.list(ingredients, IngredientInfo::toJSON),
                "description", JSON.string(description)
        );
    }

    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setTitle(obj.get("title").string());

        for (var i : obj.get("ingredients").list()) {
            var info = new IngredientInfo();
            info.loadJSON(i);
            addIngredient(info);
        }

        setDescription(obj.get("description").string());
    }
}