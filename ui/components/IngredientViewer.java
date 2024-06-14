package ui.components;

import data.IngredientInfo;

import javax.swing.*;

public class IngredientViewer extends JPanel {
    private final JLabel text = new JLabel();

    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(SwingConstants.LEFT);
        add(text);
        add(Box.createGlue());
    }

    public IngredientViewer(IngredientInfo ingredient) {
        initialise(ingredient);
    }

    private void initialise(IngredientInfo ingredient) {
        text.setText(
                ""
                + ingredient.getAmount()
                + ingredient.getIngredient().unit()
                + " "
                + ingredient.getIngredient().name()
                + " "
                + ingredient.getNotes()

        );
    }
}