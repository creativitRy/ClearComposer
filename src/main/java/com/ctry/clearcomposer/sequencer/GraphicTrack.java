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
 * A track containing notes
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ctry.clearcomposer.ClearComposer;

import com.ctry.clearcomposer.music.Chord;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public abstract class GraphicTrack
{
	private HBox track;
	private Color color;
	private Chord curChord;

	/**
	 * Constructs a new track and sets the color.
	 * @param color the color to set notes to
	 */
	public GraphicTrack(Color color)
	{
		curChord = ClearComposer.cc.getChord();
		track = new HBox();
		this.color = color;

		for (int i = 0; i < ClearComposer.cc.getNumNotes(); i++)
		{
			track.getChildren().add(new GraphicNote(color));
		}
	}

	/**
	 * Clears all the notes of this note track.
	 */
	public abstract void clearNotes();

	/**
	 * Determines whether or not this track has any notes
	 * @return true if it is devoid of notes, false if it has notes.
	 */
	public boolean isEmpty()
	{
		for (Node n : getTrack().getChildren())
		{
			if (!(n instanceof GraphicNote))
				continue;

			GraphicNote note = (GraphicNote) n;
			if (note.isOn())
				return false;
		}
		return true;
	}

	/**
	 * Saves track data to a data stream.
	 * @param out data stream to write to.
	 * @throws IOException if an error occurs during I/O
	 */
	public abstract void saveTrackData(DataOutput out) throws IOException;

	/**
	 * Loads track data from a data stream.
	 * @param in data stream to read from.
	 * @throws IOException if an error occurs during I/O
	 */
	public abstract void loadTrackData(DataInput in) throws IOException;

	/**
	 * Obtains midi pitch to be played
	 * @param index position of note to be played
	 * @return -1 if no note is played, a midi pitch otherwise
	 */
	public abstract int playNote(int index);

	public Chord getCurrentChord()
	{
		return curChord;
	}

	/**
	 * Updates this track to match the chord set in the GUI.
	 */
	public void updateChord()
	{
		curChord = ClearComposer.cc.getChord();
		updateTrackUI();
	}

	/**
	 * Updates the UI display of the track
	 */
	protected abstract void updateTrackUI();
	
	/**
	 * Getter for property 'track'.
	 * @return Value for property 'track'.
	 */
	public HBox getTrack()
	{
		return track;
	}

	/**
	 * Getter for property 'color'.
	 * @return Value for property 'color'.
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Changes the fill colors of all notes
	 * @param to color to change to
	 */
	public void changeColor(Color to)
	{
		for (Node n : getTrack().getChildren())
		{
			if (!(n instanceof GraphicNote))
				continue;

			GraphicNote note = (GraphicNote) n;

			note.changeColor(to);
		}
	}
}
