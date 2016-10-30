/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import javafx.animation.FillTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GraphicNote extends Rectangle
{
	private static final int SIZE = 25;
	private static final Color FILL_OFF = new Color(0.25, 0.25, 0.25, 1);
	private static final Duration TRANSITION_DURATION = Duration.millis(500);
	private static final Color FILL_PLAY = Color.WHITE;

	private static List<GraphicNote> notes = new ArrayList<>();

	private boolean isImmutable = false;
	private boolean isToggled = false;
	private Color fillOn;
	private Mode on;
	private FillTransition ft;

	public GraphicNote(Color fillOn)
	{
		super(SIZE, SIZE);

		getStyleClass().add("shape");

		notes.add(this);

		this.fillOn = fillOn;
		on = Mode.OFF;

		setFill(FILL_OFF);

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
	protected boolean isOn()
	{
		if (on == Mode.OFF)
			return false;

		if (on == Mode.ON_TEMP)
		{
			on = Mode.OFF;
			playColor(FILL_OFF);
		}
		else
			playColor(fillOn);

		return true;
	}

	private void playColor(Color to)
	{
		setFill(FILL_PLAY);
		ft = new FillTransition(TRANSITION_DURATION, this, FILL_PLAY, to);
		ft.setCycleCount(10);
		ft.setAutoReverse(false);
		ft.setCycleCount(1);
		ft.play();
	}

	protected void makeImmutable()
	{
		isImmutable = true;
		on = Mode.ON_PERMA;
		setFill(fillOn);
		notes.remove(this);
	}

	public void changeColor(Color to)
	{
		fillOn = to;
		if (on != Mode.OFF)
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
