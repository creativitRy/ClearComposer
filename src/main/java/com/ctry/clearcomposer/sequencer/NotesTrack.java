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
 * Contains playable notes
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.music.RelativeNote;
import javafx.scene.text.Text;

public class NotesTrack extends GraphicTrack
{
	private int octave;
	private int index;

	/**
	 * constructs a new track with no notes filled
	 * @param octave octaves higher than the lowest A
	 * @param index what note of the chord to play
	 */
	public NotesTrack(int octave, int index)
	{
		super(ClearComposer.constants.getChord().getNote(index).getPitch().getColor());

		this.octave = octave;
		this.index = index;

		Text text = new Text(formatTrackName());
		text.getStyleClass().add("text");
		getTrack().getChildren().add(0, text);
	}

	/**
	 * formatted track name to display pitch of note (no octave)
	 * @return track name
	 */
	public String formatTrackName()
	{
		return String.format("%4s: ", getNote().getFormattedPitch());
	}

	/**
	 * changes color and track name
	 */
	public void updateTrack()
	{
		((Text) getTrack().getChildren().get(0)).setText(formatTrackName());
		changeColor(getNote().getPitch().getColor());
	}

	/**
	 * gets a note represented as solfege
	 * @return
	 */
	private RelativeNote getNote()
	{
		return ClearComposer.constants.getChord().getNote(index);
	}

	/**
	 * play the midi pitch
	 * @param index position of note to be played
	 * @return midi pitch or -1 if no note is played
	 */
	public int playNote(int index)
	{
		GraphicNote note = (GraphicNote) getTrack().getChildren().get(index + 1);

		if (note.isOn())
			return getNote().getAbsolutePitch(octave);
		return -1;
	}
}
