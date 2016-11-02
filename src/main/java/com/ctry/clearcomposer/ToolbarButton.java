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

import java.net.URL;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class ToolbarButton extends StackPane
{
	private static void roundedEdges(Pane pane)
	{
		Rectangle clip = new Rectangle();
		clip.setArcHeight(10);
		clip.setArcWidth(10);
		clip.widthProperty().bind(pane.widthProperty());
		clip.heightProperty().bind(pane.heightProperty());
		pane.setClip(clip);
	}

	private Pane buttonBack;
	private Pane buttonHighlight;
	private boolean pressed;

	public ToolbarButton(String name)
	{
		buttonHighlight = new Pane();
		buttonHighlight.getStyleClass().add("highlight");

		buttonBack = new Pane();
		buttonBack.getStyleClass().add("back");

		URL url = ToolbarButton.class.getResource(name.toLowerCase() + ".png");
		if (url != null)
		{
			Image image = new Image(url.toExternalForm());
			buttonBack.setBackground(new Background(new BackgroundImage(image,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
			buttonBack.setPickOnBounds(true);
		}

		roundedEdges(this);

		Tooltip.install(this, new Tooltip(name));
		getChildren().addAll(buttonBack, buttonHighlight);
		getStyleClass().add("tblButton");
	}

	public boolean isButtonPressed()
	{
		return pressed;
	}

	public void setButtonPressed(boolean press)
	{
		pressed = press;
		if (pressed && !getStyleClass().contains("pressed"))
			getStyleClass().add("pressed");
		else if (!pressed)
			getStyleClass().remove("pressed");
	}

	@Override
	protected double computeMinWidth(double height)
	{
		return 34;
	}

	@Override
	protected double computeMinHeight(double width)
	{
		return 34;
	}

	@Override
	protected double computePrefWidth(double height)
	{
		return 34;
	}

	@Override
	protected double computePrefHeight(double width)
	{
		return 34;
	}

	@Override
	protected double computeMaxWidth(double height)
	{
		return 34;
	}

	@Override
	protected double computeMaxHeight(double width)
	{
		return 34;
	}

}
