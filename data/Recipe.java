package data;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String name;
    private final List<Category> categories = new ArrayList<>();
    private final List<RecipeStep> steps = new ArrayList<>();
    private String introduction;
}