/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import com.ctry.clearcomposer.ClearComposer;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class GraphicTrack
{
	private HBox track;
	private Color color;

	public GraphicTrack(Color color)
	{
		track = new HBox();
		this.color = color;

		for (int i = 0; i < ClearComposer.constants.getNoteAmount(); i++)
		{
			track.getChildren().add(new GraphicNote(color));
		}
	}

	/**
	 * Getter for property 'track'.
	 *
	 * @return Value for property 'track'.
	 */
	public HBox getTrack()
	{
		return track;
	}

	/**
	 * Getter for property 'color'.
	 *
	 * @return Value for property 'color'.
	 */
	public Color getColor()
	{
		return color;
	}
}
