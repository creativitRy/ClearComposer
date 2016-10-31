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
 * Chords and their five best matching notes
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import static com.ctry.clearcomposer.music.RelativeNote.DI;
import static com.ctry.clearcomposer.music.RelativeNote.DO;
import static com.ctry.clearcomposer.music.RelativeNote.FA;
import static com.ctry.clearcomposer.music.RelativeNote.FI;
import static com.ctry.clearcomposer.music.RelativeNote.LA;
import static com.ctry.clearcomposer.music.RelativeNote.MI;
import static com.ctry.clearcomposer.music.RelativeNote.RE;
import static com.ctry.clearcomposer.music.RelativeNote.RI;
import static com.ctry.clearcomposer.music.RelativeNote.SI;
import static com.ctry.clearcomposer.music.RelativeNote.SOL;
import static com.ctry.clearcomposer.music.RelativeNote.TE;
import static com.ctry.clearcomposer.music.RelativeNote.TI;

import javafx.scene.paint.Color;

public enum Chord
{
	I(0, DO, RE, MI, SOL, LA),
	ii(0, RE, MI, FA, SOL, LA),
	iii(1, RE, MI, SOL, LA, TI),
	IV(2, DO, RE, FA, SOL, LA),
	V(2, RE, FA, SOL, LA, TI),
	vi(3, DO, MI, SOL, LA, TI),
	vii$(0, TI, RE, FA, SOL, LA),
	V_ii(3, DI, MI, SOL, LA, TI),
	V_iii(4, DI, RI, FI, LA, TI),
	V_IV(0, DO, RE, MI, SOL, TE),
	V_V(1, DO, RE, MI, FI, LA),
	V_vi(1, RE, MI, FI, SI, TI),
	;

	private RelativeNote[] notes;
	private RelativeNote bass;

	Chord(int bassIndex, RelativeNote... n)
	{
		notes = n;
		bass = notes[bassIndex];
	}

	public boolean isSecondary() {
		return name().contains("_");
	}

	/**
	 * all five notes that best fit the chord
	 * @return notes
	 */
	public RelativeNote[] getNotes()
	{
		return notes.clone();
	}

	/**
	 * one of the notes of the chord
	 * @param index what note to choose
	 * @return note
	 */
	public RelativeNote getNote(int index)
	{
		return notes[index];
	}

	public RelativeNote getBassNote()
	{
		return bass;
	}

	@Override
	public String toString()
	{
		return name().replace('_', '/').replace('$', '\u00b0');
	}

	/**
	 * Gets the color of the chord as defined by the color of the root as an absolute note
	 * @return color of the chord
	 */
	public Color getColor()
	{
		return bass.getPitch().getColor();
	}
}
