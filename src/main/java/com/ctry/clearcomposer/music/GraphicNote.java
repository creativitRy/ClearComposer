/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import com.ctry.clearcomposer.ClearComposer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GraphicNote extends Rectangle
{
	private static final int ARC = 15;
	private static final Color FILL_OFF = Color.DARKGRAY;
	private static final Color BORDER = Color.GRAY;

	private Color fillOn;
	private Mode on;

	public GraphicNote(Color fillOn)
	{
		this.fillOn = fillOn;
		on = Mode.OFF;

		setArcHeight(ARC);
		setArcWidth(ARC);

		setStroke(BORDER);
		setFill(FILL_OFF);

		setOnDragEntered(t ->
		{
			if (ClearComposer.isToggle())
			{
				if (on == Mode.OFF)
					turnOn();
				else
					on = Mode.OFF;
			}
			else
			{
				if (ClearComposer.isOn())
					turnOn();
				else
					on = Mode.OFF;
			}

		});
	}

	private void turnOn()
	{
		if (ClearComposer.isPerma())
			on = Mode.ON_PERMA;
		else
			on = Mode.ON_TEMP;
	}

	public boolean isOn()
	{
		if (on == Mode.OFF)
			return false;

		if (on == Mode.ON_TEMP)
			on = Mode.OFF;

		return true;
	}

}

enum Mode
{
	OFF,
	ON_TEMP,
	ON_PERMA;
}
