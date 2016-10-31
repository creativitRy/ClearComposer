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
 * A track to display the current position being played
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BeatTrack extends GraphicTrack
{
	private static final Color DEFAULT_COLOR = Color.BLACK;

	/**
	 * makes all constructed notes immutable
	 */
	public BeatTrack()
	{
		super(DEFAULT_COLOR);

		for (Node n : getTrack().getChildren())
		{
			if (!(n instanceof GraphicNote))
				continue;

			GraphicNote note = (GraphicNote) n;

			note.makeImmutable();
		}

		//needs to be length of 6
		Text text = new Text("      ");
		text.getStyleClass().add("chordNames");
		getTrack().getChildren().add(0, text);
	}

	/**
	 * just update color
	 * @param index position being played
	 * @return -1 as in no note is being played
	 */
	@Override
	public int playNote(int index)
	{
		GraphicNote note = (GraphicNote) getTrack().getChildren().get(index + 1);

		note.isOn();

		return -1;
	}

	@Override
	public void saveTrackData(DataOutput out) throws IOException {
		//Does nothing.
	}

	@Override
	public void loadTrackData(DataInput in) throws IOException {
		//Does nothing
	}
}
