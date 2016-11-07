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
 * Contains playable notes
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.music.RelativeNote;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class BassNotesTrack extends GraphicTrack
{

	/**
	 * Constructs a new track with no notes filled
	 */
	public BassNotesTrack()
	{
		super(ClearComposer.constants.getChord().getColor());

		Text text = new Text(formatTrackName());
		text.getStyleClass().add("chordNames");
		getTrack().getChildren().add(0, text);
	}

	/**
	 * Obtains a formatted track name.
	 * @return track name that displays pitch of note without octaves
	 */
	public String formatTrackName()
	{
		return String.format("%4s: ", getNote().getFormattedPitch());
	}

	/**
	 * changes color and track name
	 */
	@Override
	public void updateTrack()
	{
		((Text) getTrack().getChildren().get(0)).setText(formatTrackName());
		changeColor(ClearComposer.constants.getChord().getColor());
	}

	/**
	 * @return a note represented as solfege
	 */
	private RelativeNote getNote()
	{
		return ClearComposer.constants.getChord().getBassNote();
	}

	/**
	 * Plays the midi pitch
	 * @param index position of note to be played
	 * @return midi pitch or -1 if no note is played
	 */
	@Override
	public int playNote(int index)
	{
		GraphicNote note = (GraphicNote) getTrack().getChildren().get(index + 1);

		if (note.isOn())
			return getNote().getPitch().getBassPitch();
		return -1;
	}

	/**
	 * Saves track data to an data stream
	 * @param out data stream to write to.
	 * @throws IOException if I/O error occurs while writing
	 */
	@Override
	public void saveTrackData(DataOutput out) throws IOException {
		List<Node> children = getTrack().getChildren();

		boolean first = true;
		out.writeInt(children.size() - 1);
		for (Node child : children)
		{
			if (first)
			{
				first = false;
				continue;
			}
			GraphicNote note = (GraphicNote) child;
			out.writeBoolean(note.isOn());
		}
	}

	/**
	 * Loads track data from a data stream.
	 * @param in data stream to read from.
	 * @throws IOException if I/O error occurs while reading
	 */
	@Override
	public void loadTrackData(DataInput in) throws IOException {
		int num = in.readInt();

		List<Node> children = getTrack().getChildren();
		for (int i = 0; i < num; i++)
		{
			if (i >= children.size() - 1)
				in.readBoolean();
			else
			{
				GraphicNote note = (GraphicNote) children.get(i + 1);
				if (in.readBoolean())
					note.turnOn(true);
				else
					note.turnOff();
			}
		}
	}
}
