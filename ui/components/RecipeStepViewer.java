package ui.components;

import ui.util.UI;

import javax.swing.*;

public class RecipeStepViewer extends JPanel {
    private final JLabel title = new JLabel();
    private final JPanel ingredients = new JPanel();


    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(UI.createLine(title));
        add(ingredients);
        add(Box.createGlue());

        ingredients.setLayout(new BoxLayout(ingredients, BoxLayout.Y_AXIS));
    }

    public RecipeStepViewer(RecipeStep step) {
        initialise(step);
    }

    private void initialise(RecipeStep step) {
        title.setText(step.getTitle());

        ingredients.removeAll();
        for (var i : step.getIngredients()) {
            ingredients.add(new IngredientViewer(i));
        }
    }
}