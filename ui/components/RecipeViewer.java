package ui.components;

import data.Recipe;
import ui.util.UI;

import javax.swing.*;

/**
 * A panel that can be used to show a recipe
 */
public class RecipeViewer extends JPanel {
    private final JLabel title = new JLabel();
    private final JPanel ingredients = new JPanel();
    private final JPanel steps = new JPanel();

    {
        var ingredientsTitle = new JLabel("Ingredients");
        var stepsTitle = new JLabel("Steps");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(UI.createLine(Box.createHorizontalStrut(20), title));
        add(Box.createVerticalStrut(20));
        add(UI.createLine(ingredientsTitle));
        add(ingredients);
        add(Box.createVerticalStrut(20));
        add(UI.createLine(stepsTitle));
        add(steps);
        add(Box.createGlue());

        {
            title.setFont(UI.getFont(30));
        }

        {
            ingredients.setLayout(new BoxLayout(ingredients, BoxLayout.Y_AXIS));
        }

        {
            steps.setLayout(new BoxLayout(steps, BoxLayout.Y_AXIS));
        }
    }

    public RecipeViewer(Recipe recipe) {
        initialise(recipe);
    }

    private void initialise(Recipe recipe) {
        title.setText(recipe.getName());

        ingredients.removeAll();
        for (var i : recipe.getIngredients()) {
            ingredients.add(new IngredientViewer(i));
        }

        steps.removeAll();
        for (var s : recipe.getSteps()) {
            steps.add(new RecipeStepViewer(s));
        }
    }
}