/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

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

	public int getPitch()
	{
		return MusicConstants.LOWEST_PITCH + ordinal();
	}
}
