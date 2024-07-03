package ui.components;

import data.ZutatInfo;

import javax.swing.*;

public class IngredientViewer extends JPanel {
    private final JLabel text = new JLabel();

    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(SwingConstants.LEFT);
        add(text);
        add(Box.createGlue());
    }

    public IngredientViewer(ZutatInfo ingredient) {
        initialise(ingredient);
    }

    private void initialise(ZutatInfo ingredient) {
        text.setText(
                ""
                + ingredient.getAmount()
                + ingredient.getIngredient().einheit()
                + " "
                + ingredient.getIngredient().name()
                + " "
                + ingredient.getNotes()

        );
    }
}