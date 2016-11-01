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
