package ui.components;

import data.Recipe;

import javax.swing.*;

public class RecipeViewer extends JPanel {
    private final Recipe recipe;

    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public RecipeViewer(Recipe recipe) {
        this.recipe = recipe;
    }
}