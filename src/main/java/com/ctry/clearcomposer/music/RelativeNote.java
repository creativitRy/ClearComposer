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
 * Note represented by solfege (relative from root of key)
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import com.ctry.clearcomposer.ClearComposer;

public enum RelativeNote
{
	DO(0),
	RE(2),
	MI(4),
	FA(5),
	SOL(7),
	LA(9),
	TI(11),
	DI(1,0,1),
	RA(1,1,-1),
	RI(3,1,1),
	ME(3,2,-1),
	FI(6,3,1),
	SE(6,4,-1),
	SI(8,4,1),
	LE(8,5,-1),
	LI(10,5,1),
	TE(10,6,-1);

	/**
	 * 0 = same as root of key, 1 = half note higher than root of key, etc
	 */
	private int steps;
	private int position;
	private int accidental;

	RelativeNote(int step)
	{
		steps = step;
		position = ordinal();
		accidental = 0;
	}

	RelativeNote(int step, int position, int accidental)
	{
		steps = step;
		this.position = position;
		this.accidental = accidental;
	}

	/**
	 * returns midi pitch with the given octave higher than the lowest A
	 *
	 * @param octave how many octaves higher than the lowest A
	 * @return midi pitch
	 */
	public int getAbsolutePitch(int octave)
	{
		return ClearComposer.constants.getKey().getNote().getAbsolutePitch() + 12 * octave + steps;
	}

	/**
	 * returns midi pitch with the same octave as the lowest A
	 *
	 * @return midi pitch
	 */
	public int getAbsolutePitch()
	{
		return getAbsolutePitch(0);
	}

	/**
	 * returns an absolute version of this relative note
	 *
	 * @return absolute note
	 */
	public AbsoluteNote getPitch()
	{
		return AbsoluteNote.values()[(ClearComposer.constants.getKey().getNote().getPitch() + steps) % 12];
	}

	public String getFormattedPitch()
	{
		//C, G, D, A, E, B, F#, C#, F, Bb, Eb, Ab, Db, Gb, Cb
		String[][] formats = {
			{"C", "D", "E", "F", "G", "A", "B"},
			{"G", "A", "B", "C", "D", "E", "F#"},
			{"D", "E", "F#", "G", "A", "B", "C#"},
			{"A", "B", "C#", "D", "E", "F#", "G#"},
			{"E", "F#", "G#", "A", "B", "C#", "D#"},
			{"B", "C#", "D#", "E", "F#", "G#", "A#"},
			{"F#", "G#", "A#", "B", "C#", "D#", "E#"},
			{"C#", "D#", "E#", "F#", "G#", "A#", "B#"},
			{"F", "G", "A", "Bb", "C", "D", "E"},
			{"Bb", "C", "D", "Eb", "F", "G", "A"},
			{"Eb", "F", "G", "Ab", "Bb", "C", "D"},
			{"Ab", "Bb", "C", "Db", "Eb", "F", "G"},
			{"Db", "Eb", "F", "Gb", "Ab", "Bb", "C"},
			{"Gb", "Ab", "Bb", "Cb", "Db", "Eb", "F"},
			{"Cb", "Db", "Eb", "Fb", "Gb", "Ab", "Bb"}
		};

		String base = formats[ClearComposer.constants.getKey().ordinal()][position];
		if (accidental == 0)
			return base;

		int acc = base.length() == 1 ? 0 : (base.charAt(1) == '#' ? 1 : (base.charAt(1) == 'b' ? -1 : 0));
		acc += accidental;
		String temp = "";
		if (acc == 1)
			temp = "#";
		else if (acc == 2)
			temp = "x";
		else if (acc == -1)
			temp = "b";
		else if (acc == -2)
			temp = "bb";

		return base.charAt(0) + temp;
	}

}
