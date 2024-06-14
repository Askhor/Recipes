package ui.components;

import data.RecipeStep;

import javax.swing.*;
import java.awt.*;

public class RecipeStepViewer extends JPanel {

    private final RecipeStep step;
    private final JPanel line1 = new JPanel();
    private final JLabel title = new JLabel();
    private final JPanel ingredients = new JPanel();


    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(line1);
        add(ingredients);

        line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
        line1.add(title);
        line1.add(Box.createGlue());

        ingredients.setLayout(new BoxLayout(ingredients, BoxLayout.Y_AXIS));
    }

    public RecipeStepViewer(RecipeStep step) {
        this.step = step;

        title.setText(step.getTitle());

        for (var i : step.getIngredients()) {
            ingredients.add(new IngredientViewer(i));
        }
    }
}