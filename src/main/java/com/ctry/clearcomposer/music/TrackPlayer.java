/**
 * Description
 *
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.sequencer.GraphicTrack;
import com.ctry.clearcomposer.sequencer.NotesTrack;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TrackPlayer
{
	private int index;
	private List<GraphicTrack> tracks;
	private Timeline timeline;

	public TrackPlayer()
	{
		index = 0;
		timeline = new Timeline(new KeyFrame(Duration.millis(ClearComposer.constants.getTempo() ), ae -> playNotes()));
		timeline.setCycleCount(Animation.INDEFINITE);

		tracks = new ArrayList<>();

		play();
	}

	private void playNotes()
	{
		for (GraphicTrack track : tracks)
		{
			int temp = track.playNote(index);
			if (temp != -1)
				MusicPlayer.playNote(temp);
		}

		index++;
		while (index >= ClearComposer.constants.getNoteAmount())
			index -= ClearComposer.constants.getNoteAmount();
	}

	public void updateDelay()
	{
		timeline.setDelay(Duration.millis(ClearComposer.constants.getTempo() ));
	}

	public void play()
	{
		timeline.play();
	}

	public void pause()
	{
		timeline.stop();
	}

	public void stop()
	{
		index = 0;
		pause();
	}

	/**
	 * Getter for property 'tracks'.
	 *
	 * @return Value for property 'tracks'.
	 */
	public List<GraphicTrack> getTracks()
	{
		return tracks;
	}
}
