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
 * Chords and their five best matching notes
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import static com.ctry.clearcomposer.music.RelativeNote.*;

public enum Chord
{
	I(DO, RE, MI, SOL, LA),
	ii(RE, MI, FA, SOL, LA),
	iii(RE, MI, SOL, LA, TI),
	IV(DO, RE, FA, SOL, LA),
	V(RE, FA, SOL, LA, TI),
	vi(DO, MI, SOL, LA, TI),
	vii(TI, RE, FA, SOL, LA),
	V_ii(DI, MI, SOL, LA, TI),
	V_iii(DI, RI, FI, LA, TI),
	V_IV(DO, RE, MI, SOL, TE),
	V_V(DO, RE, MI, FI, LA),
	V_vi(RE, MI, FI, SI, TI),
	;

	private RelativeNote[] notes;

	Chord(RelativeNote n0, RelativeNote n1, RelativeNote n2, RelativeNote n3, RelativeNote n4)
	{
		notes = new RelativeNote[]{n0, n1, n2, n3, n4};
	}

	/**
	 * all five notes that best fit the chord
	 * @return notes
	 */
	public RelativeNote[] getNotes()
	{
		return notes;
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

	public String toString()
	{
		return name().replace('_', '/');
	}
}
