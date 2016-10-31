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

import com.ctry.clearcomposer.music.Chord;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChordButton extends StackPane {
	private static void roundedEdges(Pane pane)
	{
		Rectangle clip = new Rectangle();
		clip.setArcHeight(10);
		clip.setArcWidth(10);
		clip.widthProperty().bind(pane.widthProperty());
		clip.heightProperty().bind(pane.heightProperty());
		pane.setClip(clip);
	}

	private StackPane button;
	private Pane buttonBack;
	private Pane buttonHighlight;
	private Label buttonText;
	private boolean pressed;

	public ChordButton(Chord c)
	{
		button = new StackPane();

		buttonBack = new Pane();
		buttonBack.getStyleClass().add("back");
		roundedEdges(buttonBack);

		buttonHighlight = new Pane();
		buttonHighlight.getStyleClass().add("highlight");
		roundedEdges(buttonHighlight);

		buttonText = new Label(c.toString());
		buttonText.setPadding(new Insets(5));

		button.getChildren().addAll(buttonBack, buttonHighlight, buttonText);
		button.getStyleClass().addAll("ccbutton");

		//Set size
		button.setMinSize(100, 35);
		button.setPrefSize(100, 35);
		button.setMaxSize(100, 35);

		getChildren().add(button);
		//setEffect(new DropShadow(3, 2, 2, Color.gray(0, .3)));
	}

	public boolean isButtonPressed() {
		return pressed;
	}

	public void setButtonPressed(boolean pressed) {
		this.pressed = pressed;
		if (pressed)
			button.getStyleClass().add("pressed");
		else
			button.getStyleClass().remove("pressed");
	}
}
