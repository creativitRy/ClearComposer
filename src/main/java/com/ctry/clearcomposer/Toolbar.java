package com.ctry.clearcomposer;

import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

public class Toolbar extends HBox
{
	private HashMap<String, ToolbarButton> buttons = new HashMap<>();
	
	public Toolbar()
	{
		super(5);
		setPadding(new Insets(5));
		getStyleClass().add("Toolbar");
	}
	
	public void addRegularButton(String action, Runnable onAction)
	{
		ToolbarButton button = buttons.get(action);
		if (button == null)
		{
			button = new ToolbarButton(action);
			buttons.put(action, button);
			getChildren().add(button);
		}
		ToolbarButton f_button = button;
		button.setOnMousePressed(evt -> f_button.setButtonPressed(true));
		button.setOnMouseReleased(evt -> f_button.setButtonPressed(false));
		button.setOnMouseClicked(evt -> onAction.run());
	}
	
	public void removeButton(String action)
	{
		ToolbarButton but = buttons.remove(action);
		if (but != null)
			getChildren().remove(but);
	}
}
