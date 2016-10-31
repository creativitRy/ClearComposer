package com.ctry.clearcomposer;

import java.util.HashMap;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

public class Toolbar extends HBox
{
	private HashMap<String, ToolbarButton> buttons = new HashMap<>();
	
	public Toolbar()
	{
		super(10);
		setPadding(new Insets(10));
	}
	
	public void addAction(String action, Runnable onAction)
	{
		//TODO
		//if (buttons.containsKey(action))
			//buttons.get(buttons.)
	}
}
