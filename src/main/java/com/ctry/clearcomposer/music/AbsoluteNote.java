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
 * Music note represented as a note name
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import javafx.scene.paint.Color;

public enum AbsoluteNote
{
	A,
	A$,
	B,
	C,
	C$,
	D,
	D$,
	E,
	F,
	F$,
	G,
	G$;

	/**
	 * lowest midi pitch that is the same note
	 * @return midi pitch
	 */
	public int getAbsolutePitch()
	{
		return MusicConstants.LOWEST_PITCH + ordinal();
	}

	/**
	 * a = 0, a# = 1, etc
	 * @return
	 */
	public int getPitch()
	{
		return ordinal();
	}

	//TODO: when to use flats and when to use sharps

	/**
	 * note name
	 * @return note name
	 */
	public String toString()
	{
		return name().replace('$', '#');
	}

	/**
	 * color that represents the pitch
	 * @return color
	 */
	public Color getColor()
	{
		ordinal();
		return Color.hsb(ordinal() * 360 / 11.0, 1, 1);
	}
}
