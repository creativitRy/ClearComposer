/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GraphicNote extends Rectangle
{
	private static final int SIZE = 25;
	private static final int ARC = 15;
	private static final double STROKE_WIDTH = 3;
	private static final Color BORDER = Color.GRAY;
	private static final Color FILL_OFF = Color.DARKGRAY;

	private static final Color FILL_PLAY = Color.WHITE;

	private static List<GraphicNote> notes = new ArrayList<>();

	private boolean isToggled = false;
	private Color fillOn;
	private Mode on;

	public GraphicNote(Color fillOn)
	{
		super(SIZE, SIZE);

		notes.add(this);

		this.fillOn = fillOn;
		on = Mode.OFF;

		setArcHeight(ARC);
		setArcWidth(ARC);

		setStroke(BORDER);
		setStrokeWidth(STROKE_WIDTH);

		setFill(FILL_OFF);

		setOnDragDetected(t -> startFullDrag());

		setOnMouseDragEntered(t ->
		{
			if (ClearComposer.isToggle())
			{
				if (!isToggled)
				{
					if (on == Mode.OFF)
						turnOn();
					else
						turnOff();

					isToggled = true;
				}
			}
			else
			{
				if (t.isPrimaryButtonDown())
					turnOn();
				else
					turnOff();
			}


		});

	}

	private void turnOn()
	{
		if (ClearComposer.isPerma())
			on = Mode.ON_PERMA;
		else
			on = Mode.ON_TEMP;

		setFill(fillOn);
	}

	private void turnOff()
	{
		on = Mode.OFF;

		setFill(FILL_OFF);
	}

	public boolean isOn()
	{
		if (on == Mode.OFF)
			return false;

		if (on == Mode.ON_TEMP)
			on = Mode.OFF;

		return true;
	}

	public static void stopToggle()
	{
		for (GraphicNote note : notes)
		{
			note.isToggled = false;
		}
	}

}

enum Mode
{
	OFF,
	ON_TEMP,
	ON_PERMA;
}
