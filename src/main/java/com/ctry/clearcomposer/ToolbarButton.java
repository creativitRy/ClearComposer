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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

	private Pane buttonBack;
	private ImageView buttonImg;
	private Pane buttonHighlight;
	private Label buttonText;
	private Tooltip description;

	private StringProperty action;
	private BooleanProperty pressed;

	private Image toolbarImage;
	private Image disabledImage;

	public ToolbarButton(String name)
	{
		description = new Tooltip(name);

		buttonHighlight = new Pane();
		buttonHighlight.getStyleClass().add("highlight");

		buttonBack = new StackPane();
		buttonBack.getStyleClass().add("back");
		buttonBack.setPadding(new Insets(4));
		getChildren().addAll(buttonBack, buttonHighlight);

		buttonImg = new ImageView();
		buttonText = new Label(name);
		setButtonAction(name);

		disabledProperty().addListener((val, before, after) -> updateState());
		
		roundedEdges(this);
		Tooltip.install(this, description);
		getStyleClass().add("tblButton");

		addEventHandler(MouseEvent.ANY, evt -> evt.consume());
	}

	public final String getButtonAction()
	{
		return action != null ? action.get() : null;
	}

	public final void setButtonAction(String action)
	{
		buttonActionProperty().set(action);
	}

	public final StringProperty buttonActionProperty()
	{
		if (action == null)
		{
			action = new SimpleStringProperty(this, "buttonAction")
			{
				@Override
				protected void invalidated()
				{
					String name = get();
					description.setText(name);

					String path = name.toLowerCase().replace(' ', '_');
					URL url = ToolbarButton.class.getResource(path + ".png");
					if (url != null)
					{

						toolbarImage = new Image(url.toExternalForm());
						URL disabledUrl = ToolbarButton.class.getResource(path + "_disabled.png");
						disabledImage = disabledUrl == null ? toolbarImage : new Image(disabledUrl.toExternalForm());
						buttonBack.getChildren().clear();
						buttonBack.getChildren().add(buttonImg);
					}
					else
					{
						buttonText.setText(name);
						buttonBack.getChildren().clear();
						buttonBack.getChildren().add(buttonText);
					}
					updateState();
				}
			};
		}
		return action;
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

	private void updateState()
	{
		if (toolbarImage != null)
			buttonImg.setImage(isDisabled() ? disabledImage : toolbarImage);
		else
			buttonText.setTextFill(isDisabled() ? Color.LIGHTGRAY : Color.WHITE);
	}

}
