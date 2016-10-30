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
	TI(11);

	/**
	 * 0 = same as root of key, 1 = half note higher than root of key, etc
	 */
	private int steps;

	RelativeNote(int step)
	{
		steps = step;
	}

	/**
	 * returns midi pitch with the given octave higher than the lowest A
	 * @param octave how many octaves higher than the lowest A
	 * @return midi pitch
	 */
	public int getAbsolutePitch(int octave)
	{
		return ClearComposer.constants.getKey().getAbsolutePitch() + 12 * octave + steps;
	}

	/**
	 * returns midi pitch with the same octave as the lowest A
	 * @return midi pitch
	 */
	public int getAbsolutePitch()
	{
		return getAbsolutePitch(0);
	}

	/**
	 * returns an absolute version of this relative note
	 * @return
	 */
	public AbsoluteNote getPitch()
	{
		return AbsoluteNote.values()[(ClearComposer.constants.getKey().getPitch() + steps) % 12];
	}

}
