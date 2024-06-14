package data;

import files.json.JSON;
import files.json.JSONFormatException;
import files.json.JSONObject;
import files.json.JSONValue;
import util.AList;

import java.util.*;

/**
 * Represents entire recipes
 */
public class Recipe {
    private String name;
    /**
     * The categories with which this recipe will be associated
     */
    private final Set<Category> categories = new HashSet<>();
    /**
     * The separate steps in this recipe
     */
    private final AList<RecipeStep> steps = new AList<>();
    /**
     * A short (or lengthy) introduction to the recipe
     */
    private String introduction;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Set<Category> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public List<RecipeStep> getSteps() {
        return steps.view;
    }

    public void addCategory(Category c) {
        categories.add(c);
    }

    public void removeCategory(Category c) {
        categories.remove(c);
    }

    public void addStep(RecipeStep step) {
        steps.add(step);
    }

    public void removeStep(RecipeStep step) {
        steps.remove(step);
    }

    /**
     * Finds and add up all the ingredients that each step of the recipe needs
     */
    public List<IngredientInfo> getIngredients() {
        var out = new ArrayList<IngredientInfo>();
        for (var step : steps) {
            out.addAll(step.getIngredients());
        }
        return out;
    }

    public JSONObject toJSON() {
        return JSON.object(
                "name", JSON.string(getName()),
                "categories", JSON.list(getCategories(), c -> JSON.string(c.getName())),
                "introduction", JSON.string(getIntroduction()),
                "steps", JSON.list(getSteps(), RecipeStep::toJSON)
        );
    }

    public void loadJSON(JSONValue json) throws JSONFormatException {
        var obj = json.object();
        setName(obj.get("name").string());

        for (var c : obj.get("categories").list())
            addCategory(Category.get(c.string()));

        setIntroduction(obj.get("introduction").string());

        for (var s : obj.get("steps").list()) {
            var step = new RecipeStep();
            step.loadJSON(s);
            addStep(step);
        }
    }
}