/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee
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
 * Note that can be played and displayed on screen
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import javafx.animation.FillTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GraphicNote extends Rectangle
{
	private static final int SIZE = 25;
	private static final Color FILL_OFF = new Color(0.25, 0.25, 0.25, 1);
	private static final Duration TRANSITION_DURATION = Duration.millis(500);
	private static final Color FILL_PLAY = Color.WHITE;

	private static List<GraphicNote> notes = new ArrayList<>();

	private boolean isImmutable = false;
	private boolean isToggled = false;
	private Color fillOn;
	private Mode on;
	private FillTransition ft;

	/**
	 * creates a new playable graphical note with the given potential color
	 * @param fillOn color if note will be played
	 */
	public GraphicNote(Color fillOn)
	{
		super(SIZE, SIZE);

		getStyleClass().add("shape");

		this.fillOn = fillOn;
		on = Mode.OFF;

		setFill(FILL_OFF);

		setOnMousePressed(this::mouseAction);
		setOnMouseDragEntered(this::mouseAction);
	}

	/**
	 * either turns on the note or turns off depending on the action of the mouse
	 * @param t
	 */
	private void mouseAction(MouseEvent t)
	{
		if (ClearComposer.isToggle())
		{
			if (!isToggled)
			{
				if (on == Mode.OFF)
					turnOn();
				else
					turnOff();

				isToggled = true;
				notes.add(this);
			}
		}
		else
		{
			if (t.isPrimaryButtonDown())
				turnOn();
			else
				turnOff();
		}
	}

	/**
	 * turns on note
	 */
	private void turnOn()
	{
		if (isImmutable)
			return;

		if (ClearComposer.isPerma())
			on = Mode.ON_PERMA;
		else
			on = Mode.ON_TEMP;

		setFill(fillOn);
	}

	/**
	 * turns off note
	 */
	private void turnOff()
	{
		if (isImmutable)
			return;

		on = Mode.OFF;

		setFill(FILL_OFF);
	}

	/**
	 * if on is temporary, changes it to off
	 * @return true if on, false otherwise
	 */
	protected boolean isOn()
	{
		if (on == Mode.OFF)
			return false;

		if (on == Mode.ON_TEMP)
		{
			on = Mode.OFF;
			playColor(FILL_OFF);
		}
		else
			playColor(fillOn);

		return true;
	}

	/**
	 * changes color to white and fades it back to ordinary color
	 * @param to color to fade to
	 */
	private void playColor(Color to)
	{
		setFill(FILL_PLAY);
		ft = new FillTransition(TRANSITION_DURATION, this, FILL_PLAY, to);
		ft.setCycleCount(10);
		ft.setAutoReverse(false);
		ft.setCycleCount(1);
		ft.play();
	}

	/**
	 * makes the note unable to be turned off from mouse events
	 * to be used with beat tracks
	 */
	protected void makeImmutable()
	{
		isImmutable = true;
		on = Mode.ON_PERMA;
		setFill(fillOn);
		notes.remove(this);
	}

	/**
	 * changes the filled color for the note wihtout transitions
	 * @param to color to change to
	 */
	public void changeColor(Color to)
	{
		fillOn = to;
		if (on != Mode.OFF)
			setFill(fillOn);
	}

	/**
	 * when toggling, you don't want the notes that were toggled to be toggled again
	 * therefore they are stored in a static list
	 * calling this method clears the list and makes the notes toggle-able again
	 */
	public static void stopToggle()
	{
		for (GraphicNote note : notes)
		{
			note.isToggled = false;
		}

		notes.clear();
	}

}

/**
 * off = note is turned off
 * on_temporary = note is turned on but will be switched off once played
 * on_permanent = note will be on forever
 */
enum Mode
{
	OFF,
	ON_TEMP,
	ON_PERMA;
}
