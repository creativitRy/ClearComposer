/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.sequencer;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class TrackMaster extends GraphicTrack
{
	private static final Color DEFAULT_COLOR = Color.BLACK;

	public TrackMaster()
	{
		super(DEFAULT_COLOR);

		for (Node n : getTrack().getChildren())
		{
			if (!(n instanceof GraphicNote))
				continue;

			GraphicNote note = (GraphicNote) n;

			note.makeImmutable();
		}
	}
}
