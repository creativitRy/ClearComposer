/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BeatTrack extends GraphicTrack
{
	private static final Color DEFAULT_COLOR = Color.BLACK;

	public BeatTrack()
	{
		super(DEFAULT_COLOR);

		for (Node n : getTrack().getChildren())
		{
			if (!(n instanceof GraphicNote))
				continue;

			GraphicNote note = (GraphicNote) n;

			note.makeImmutable();
		}

		//needs to be length of 6
		Text text = new Text("      ");
		text.getStyleClass().add("text");
		getTrack().getChildren().add(0, text);
	}

	public int playNote(int index)
	{
		GraphicNote note = (GraphicNote) getTrack().getChildren().get(index + 1);

		note.isOn();

		return -1;
	}
}
