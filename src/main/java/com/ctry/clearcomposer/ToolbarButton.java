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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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

	private String actionName;
	private Pane buttonBack;
	private Pane buttonHighlight;
	private BooleanProperty pressed; //Lazily create property
	

	public ToolbarButton(String name)
	{
		actionName = name;
		
		buttonHighlight = new Pane();
		buttonHighlight.getStyleClass().add("highlight");

		buttonBack = new Pane();
		buttonBack.getStyleClass().add("back");
		getChildren().addAll(buttonBack, buttonHighlight);
		
		URL url = ToolbarButton.class.getResource(name.toLowerCase().replace(' ', '_') + ".png");
		if (url != null)
		{
			Image image = new Image(url.toExternalForm());
			buttonBack.setBackground(new Background(new BackgroundImage(image,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
			buttonBack.setPickOnBounds(true);
			buttonBack.setPrefSize(image.getWidth() + 8, image.getHeight() + 8);
		}
		else
		{
			Label text = new Label(name);
			text.setTextFill(Color.WHITE);
			getChildren().add(text);
		}

		roundedEdges(this);
		Tooltip.install(this, new Tooltip(name));
		getStyleClass().add("tblButton");
	}

	public String getActionName()
	{
		return actionName;
	}
	
	public final boolean isButtonPressed()
	{
		return pressed != null && pressed.get();
	}

	public final void setButtonPressed(boolean press)
	{
		buttonPressedProperty().set(press);
	}

	public final BooleanProperty buttonPressedProperty()
	{
		if (pressed == null)
		{
			pressed = new SimpleBooleanProperty(this, "pressed")
			{
				@Override
				protected void invalidated()
				{
					boolean pressed = get();
					if (pressed && !getStyleClass().contains("pressed"))
						getStyleClass().add("pressed");
					else if (!pressed)
						getStyleClass().remove("pressed");					
				}
			};
		}
		return pressed;
	}
	
	@Override
	protected double computeMaxWidth(double height)
	{
		return prefWidth(height);
	}

	@Override
	protected double computeMaxHeight(double width)
	{
		return prefHeight(width);
	}

}
