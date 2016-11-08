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
 * Note that can be played and displayed on screen
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import java.util.HashMap;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.history.NotesEntry;

import javafx.animation.Transition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GraphicNote extends Rectangle
{

	private static final Color PERMA_STROKE = Color.GRAY;
	
	private static final int SIZE = 25;
	private static final Color FILL_OFF = new Color(0.25, 0.25, 0.25, 1);
	private static final Duration TRANSITION_DURATION = Duration.millis(500);
	private static final Color FILL_PLAY = Color.WHITE;

	private static HashMap<GraphicNote, NotePrevState> noteStates = new HashMap<>();

	private boolean isImmutable = false;
	private boolean isTouched = false;
	private Color fillOn;
	private NotePlayState on;
	private Transition ft;

	/**
	 * creates a new playable graphical note with the given potential color
	 * @param fillOn color if note will be played
	 */
	public GraphicNote(Color fillOn)
	{
		super(SIZE, SIZE);

		getStyleClass().add("shape");

		this.fillOn = fillOn;
		on = NotePlayState.OFF;

		setFill(FILL_OFF);
		setStroke(PERMA_STROKE);

		setOnMousePressed(this::mouseAction);
		setOnMouseDragEntered(this::mouseAction);
	}

	/**
	 * either turns on the note or turns off depending on the action of the mouse
	 * @param t mouse event
	 */
	private void mouseAction(MouseEvent t)
	{
		if (!isTouched)
		{
			//We need this to make change
			if (ClearComposer.cc.isToggle() || (on != NotePlayState.OFF ^ t.isPrimaryButtonDown()))
			{
				boolean isOn = on != NotePlayState.OFF;
				boolean isPerma = isOn ? on == NotePlayState.ON_PERMA : ClearComposer.cc.isPerma();
				noteStates.put(this, new NotePrevState(isOn, isPerma));

				isTouched = true;
				if (ClearComposer.cc.isToggle())
					toggle(ClearComposer.cc.isPerma());
				else {
					if (t.isPrimaryButtonDown())
						turnOn(ClearComposer.cc.isPerma());
					else
						turnOff();
				}
			}
		}
	}

	/**
	 * toggles notes
	 * @param isPerma true if change to permanent. false if change to temporary
	 */
	public void toggle(boolean isPerma)
	{
		if (on == NotePlayState.OFF)
			turnOn(isPerma);
		else
			turnOff();
	}

	/**
	 * turns on note
	 */
	void turnOn(boolean isPerma)
	{
		if (isImmutable)
			return;

		if (isPerma)
			on = NotePlayState.ON_PERMA;
		else
			on = NotePlayState.ON_TEMP;

		setStroke(isPerma ? PERMA_STROKE : fillOn.invert());
		setFill(fillOn);
	}

	/**
	 * turns off note
	 */
	void turnOff()
	{
		if (isImmutable)
			return;

		on = NotePlayState.OFF;

		setStroke(PERMA_STROKE);
		setFill(FILL_OFF);
	}

	public NotePlayState getPlayState()
	{
		return on;
	}

	/**
	 * if on is temporary, changes it to off
	 * @return true if on, false otherwise
	 */
	protected boolean isOn()
	{
		if (on == NotePlayState.OFF)
			return false;

		if (on == NotePlayState.ON_TEMP)
			on = NotePlayState.OFF;
		
		playColor();
		return true;
	}

	/**
	 * changes color to white and fades it back to ordinary color
	 */
	private void playColor()
	{
		Color stroke = (Color)getStroke();
		ft = new Transition()
		{
			{
				setCycleDuration(TRANSITION_DURATION);
			}
			@Override
			protected void interpolate(double frac)
			{
				if (on != NotePlayState.OFF)
				{
					setFill(FILL_PLAY.interpolate(fillOn, frac));
					setStroke(stroke.interpolate(on == NotePlayState.ON_PERMA ? PERMA_STROKE : fillOn.invert(), frac));
				}
				else
				{
					setFill(FILL_PLAY.interpolate(FILL_OFF, frac));
					setStroke(stroke.interpolate(PERMA_STROKE, frac));
				}
			}
		};
		ft.play();
	}

	/**
	 * makes the note unable to be turned off from mouse events
	 * to be used with beat tracks
	 */
	protected void makeImmutable()
	{
		isImmutable = true;
		on = NotePlayState.ON_PERMA;
		setFill(fillOn);
		setStroke(PERMA_STROKE);
		noteStates.remove(this);
	}

	/**
	 * changes the filled color for the note wihtout transitions
	 * @param to color to change to
	 */
	public void changeColor(Color to)
	{
		fillOn = to;
		if (on != NotePlayState.OFF)
			setFill(fillOn);
	}

	/**
	 * This is called when the user finishes editing a series of notes.
	 * For example this will record the note editing move. This will also
	 * reset the flag that the user has 'touched' or selected a note.
	 */
	public static void finishNotesEditing()
	{
		if (!noteStates.isEmpty())
			ClearComposer.cc.pushMove(new NotesEntry(new HashMap<>(noteStates)));
		for (GraphicNote note : noteStates.keySet())
			note.isTouched = false;
		noteStates.clear();
	}

}

