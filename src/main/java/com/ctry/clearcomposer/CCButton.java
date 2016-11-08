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


/**
 * Stylized chord buttons
 *
 * @author theKidOfArcrania
 * Date: 10/29/2016.
 */

package com.ctry.clearcomposer;

import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class CCButton extends StackPane
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

	private Pane buttonHighlight;
	private Label buttonText;
	private BooleanProperty pressed; //Lazily create property
	private ObjectProperty<Paint> textFill;


	public CCButton(String text, Color textFill)
	{
		buttonHighlight = new Pane();
		buttonHighlight.getStyleClass().add("highlight");
		roundedEdges(buttonHighlight);

		buttonText = new Label(text);
		buttonText.setPadding(new Insets(5));
		setTextFill(textFill);

		getChildren().addAll(buttonHighlight, buttonText);
		getStyleClass().addAll("ccbutton");
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

	public final Paint getTextFill()
	{
		return textFill == null ? null : textFill.get();
	}

	public final void setTextFill(Paint textFill)
	{
		textFillProperty().set(textFill);
	}

	public final ObjectProperty<Paint> textFillProperty()
	{
		if (textFill == null)
		{
			textFill = new SimpleObjectProperty<Paint>(this, "textFill")
			{
				@Override
				protected void invalidated()
				{
					buttonText.setTextFill(get());
				}
			};
		}
		return textFill;
	}

	public void setBorder(Color color, double width)
	{
		buttonHighlight.setBorder(new Border(
			new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(width))));
	}
}
