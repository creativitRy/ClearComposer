/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GraphicNote extends Rectangle
{
	private static final int SIZE = 25;
	private static final Color FILL_OFF = new Color(0.25, 0.25, 0.25, 1);

	private static final Color FILL_PLAY = Color.WHITE;

	private static List<GraphicNote> notes = new ArrayList<>();

	private boolean isImmutable = false;
	private boolean isToggled = false;
	private Color fillOn;
	private Mode on;

	public GraphicNote(Color fillOn)
	{
		super(SIZE, SIZE);


		getStyleClass().add("shape");

		notes.add(this);

		this.fillOn = fillOn;
		on = Mode.OFF;

		setFill(FILL_OFF);

		setOnDragDetected(t -> startFullDrag());

		setOnMousePressed(this::mouseAction);
		setOnMouseDragEntered(this::mouseAction);

	}

	private void mouseAction(MouseEvent t)
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
	}

	private void turnOn()
	{
		if (isImmutable)
			return;

		if (ClearComposer.isPerma())
			on = Mode.ON_PERMA;
		else
			on = Mode.ON_TEMP;

		setFill(fillOn);
	}

	private void turnOff()
	{
		if (isImmutable)
			return;

		on = Mode.OFF;

		setFill(FILL_OFF);
	}

	/**
	 * if on is temporary, changes it to off
	 * @return true if on, false otherwise
	 */
	public boolean isOn()
	{
		if (on == Mode.OFF)
			return false;

		if (on == Mode.ON_TEMP)
			on = Mode.OFF;

		return true;
	}

	protected void makeImmutable()
	{
		isImmutable = true;
		on = Mode.ON_PERMA;
		setFill(fillOn);
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
