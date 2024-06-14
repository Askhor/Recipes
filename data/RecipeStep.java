package data;

import util.AList;

import java.util.List;

public class RecipeStep {
    private final AList<IngredientInfo> ingredients = new AList<>();
    private String title;
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
}