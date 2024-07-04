package ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import data.Rezept;

public class Fenster extends JFrame {
	
	private Collection<Rezept> alleRezepte;
	private DefaultListModel<Rezept> rezeptListModel;
	private JList<Rezept> rezeptList;
	
	public Fenster() {

		setTitle("Rezeptbuch");
		setSize(720, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLayout(new BorderLayout());

//		Menü-Bar:
		JPanel sideMenu = new JPanel(new GridLayout(1, 2));
		JButton addButton = new JButton("Rezept hinzufügen");
		JButton deleteButton = new JButton("Rezept löschen");
		sideMenu.add(addButton);
		sideMenu.add(deleteButton);
		add(sideMenu, BorderLayout.SOUTH);
		
		addButton.addActionListener(e -> addRezept());
		deleteButton.addActionListener(e -> deleteRezept());

//		Rezept-Liste:
		rezeptListModel = new DefaultListModel<>();
		rezeptList = new JList<>(rezeptListModel);
		JPanel panelListe = new JPanel();
		panelListe.add(rezeptList);
		updateRezeptList();
		add(new JScrollPane(rezeptList), BorderLayout.CENTER);
		
	}
	
	private void updateRezeptList() {
		rezeptListModel.clear();
		for (Rezept rezept : Rezept.getAlleRezepte()) {
			rezeptListModel.addElement(rezept);
		}
	}
	
	private void addRezept() {
		String name = JOptionPane.showInputDialog(this, "Name des Rezepts: ");
		if (name != null && !name.isEmpty()) {
			Rezept neuesRezept = new Rezept();
			neuesRezept.setName(name);
			Rezept.getAlleRezepte().add(neuesRezept);
			neuesRezept.speicher();
			updateRezeptList();
		}
	}

	private void deleteRezept() {
		Rezept selectedRezept = rezeptList.getSelectedValue();
		if (selectedRezept != null) {
			int response = JOptionPane.showConfirmDialog(this, "Wollen Sie das Rezept löschen?", "Rezept löschen", JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.YES_OPTION) {
				Rezept.getAlleRezepte().remove(selectedRezept);
				selectedRezept.speicher();
				updateRezeptList();
			}
		} else {
			JOptionPane.showMessageDialog(this, "Wählen Sie ein Rezept aus, das gelöscht werden soll.", "Kein Rezept ausgewählt", JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Fenster frame = new Fenster();
		});
	}

}
