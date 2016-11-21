/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee and Henry "theKidOfArcrania" Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ctry.clearcomposer;

import java.util.HashMap;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;

public class Toolbar extends FlowPane
{
	private HashMap<String, ToolbarButton> buttons = new HashMap<>();

	public Toolbar()
	{
		super(5, 5);
		setPadding(new Insets(5));
		setAlignment(Pos.CENTER_LEFT);
		getStyleClass().add("panel");
	}

	/**
	 * Adds a toggle toolbar button that toggles between
	 * on and off state whenever the user clicks on the button.
	 * @param action name of button.
	 * @param permState state of whether if button is toggled or not.
	 * @param onAction called whenever user clicks on button. Passes in button's previous state
	 * @return the created button.
	 */
	public ToolbarButton addToggleButton(String action, BooleanProperty permState, Runnable onAction)
	{
		ToolbarButton button = addButton(action);
		button.setButtonPressed(permState.get());
		button.setOnMouseReleased(null);
		button.setOnMousePressed(evt -> button.setButtonPressed(true));
		button.setOnMouseClicked(evt -> {
			boolean pressed = permState.get();
			button.setButtonPressed(!pressed);
			permState.set(!pressed);
			if (onAction != null)
				onAction.run();
		});
		return button;
	}

	/**
	 * Adds a button (with no events attached to it)
	 * @param action name of button.
	 * @return the created button
	 */
	public ToolbarButton addButton(String action)
	{
		ToolbarButton button = buttons.get(action);
		if (button == null)
		{
			button = new ToolbarButton(action);
			buttons.put(action, button);
			getChildren().add(button);
		}
		return button;
	}

	/**
	 * Adds a regular button that will press-release after clicking it
	 * @param action name of button.
	 * @param onAction called whenever user clicks on the button
	 * @return the created button
	 */
	public ToolbarButton addRegularButton(String action, Runnable onAction)
	{
		ToolbarButton button = addButton(action);
		button.setOnMousePressed(evt -> button.setButtonPressed(true));
		button.setOnMouseReleased(evt -> button.setButtonPressed(false));
		if (onAction != null)
			button.setOnMouseClicked(evt -> onAction.run());
		return button;
	}

	public <T> CCComboBox<T> addComboBox(String tooltip, Runnable onChange, int selectedIndex, T... options)
	{
		class Val {
			T val;
		}
		Val before = new Val();
		CCComboBox<T> comboBox = new CCComboBox<>();
		comboBox.getItems().addAll(options);
		comboBox.getSelectionModel().select(selectedIndex);
		comboBox.setTooltip(new Tooltip(tooltip));
		comboBox.setFocusTraversable(false);
		comboBox.setOnAction(evt -> {
			T now = comboBox.getValue();
			if (now != before.val)
			{
				if (!comboBox.isIgnoreChanges())
					onChange.run();
				before.val = now;
			}
		});
		getChildren().add(comboBox);
		return comboBox;
	}

	public void addNode(Node n)
	{
		getChildren().add(n);
	}
	
	public Slider addSlider(String action, Runnable onChange, double min, double max, double value)
	{
		Slider slider = new Slider(min, max, value);
		slider.setFocusTraversable(false);
		slider.valueProperty().addListener(val -> onChange.run());
		getChildren().add(slider);
		Tooltip.install(slider, new Tooltip(action));
		return slider;
	}

	public void addSeparator()
	{
		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		separator.setMaxWidth(34);
		getChildren().add(separator);
	}

//	public void removeNode(Node n)
//	{
//		if (n instanceof ToolbarButton) {
//			removeButton(((ToolbarButton) n).);
//			buttons.ke
//		}
//		else
//			getChildren().remove(n);
//	}
	
//	public void removeButton(String action)
//	{
//		ToolbarButton but = buttons.remove(action);
//		if (but != null)
//			getChildren().remove(but);
//	}
}
