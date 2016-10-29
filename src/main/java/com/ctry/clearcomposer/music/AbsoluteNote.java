/**
 * Description
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

	public int getAbsolutePitch()
	{
		return MusicConstants.LOWEST_PITCH + ordinal();
	}

	public int getPitch()
	{
		return ordinal() % 12;
	}

	//TODO: when to use flats and when to use sharps
	public String toString()
	{
		return name().replace('$', '#');
	}

	public Color getColor()
	{
		ordinal();
		return Color.hsb(ordinal() * 360 / 11.0, 1, 1);
	}
}
