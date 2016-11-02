/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee and Henry "theKidOfArcrania" Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Plays the tracks
 *
 * @author creativitRy, theKidOfArcrania
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.sequencer.GraphicTrack;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TrackPlayer
{
	private int index;
	private List<GraphicTrack> tracks;
	private Timeline timeline;
	private Status playState;

	private static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... params)
	{
		try
		{
			return type.getConstructor(params);
		} catch (NoSuchMethodException e)
		{
			return null;
		}
	}

	/**
	 * constructs a new track player with multiple blank tracks
	 */
	public TrackPlayer()
	{
		index = 0;
		timeline = new Timeline(new KeyFrame(Duration.millis(ClearComposer.constants.getTempo()), ae -> playNotes()));
		timeline.setCycleCount(Animation.INDEFINITE);

		tracks = new ArrayList<>();

		//TODO
		//Don't play by default
		//play();
	}

	/**
	 * Saves track data to an byte stream.
	 * Note that this will not close the file.
	 *
	 * @param out byte stream to write to.
	 * @throws IOException when an I/O error occurs while writing
	 */
	public void saveTracks(OutputStream out) throws IOException
	{
		//TODO: constants;
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(tracks.size());
		for (GraphicTrack track : tracks)
		{
			oos.writeObject(track.getClass());
			track.saveTrackData(oos);
		}
		oos.flush();
	}

	/**
	 * Loads track data from an byte stream.
	 * Note that this will not close the file.
	 *
	 * @param in byte stream to write to.
	 * @throws IOException when an I/O error occurs while writing
	 */
	public void loadTracks(InputStream in) throws IOException
	{
		//TODO: should we put MusicConstants
		ObjectInputStream ois = new ObjectInputStream(in);
		int trackNum = ois.readInt();

		for (int i = 0; i < trackNum; i++)
		{
			try
			{
				GraphicTrack track = getTracks().get(i);
				Class<?> trackClass = (Class<?>) ois.readObject();
				if (!GraphicTrack.class.isAssignableFrom(trackClass))
					throw new FileCorruptionException("File Corruption: Track not instanceof GraphicsTrack");
				if (trackClass != track.getClass())
					throw new FileCorruptionException("File Corruption: Tracks mismatch");
				track.loadTrackData(ois);
			} catch (ClassNotFoundException e)
			{
				throw new FileCorruptionException("File Corruption: Unable to locate class");
			}
		}
	}


	/**
	 * plays all the notes in the current position
	 */
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

	/**
	 * changes the delay of the timeline to match the new tempo
	 */
	public void updateDelay()
	{
		timeline.setDelay(Duration.millis(ClearComposer.constants.getTempo()));
	}

	public Status getPlayState()
	{
		return timeline.getStatus();
	}

	/**
	 * play
	 */
	public void play()
	{
		if (timeline.getStatus() != Status.RUNNING)
			timeline.play();
	}

	/**
	 * stop at current position
	 */
	public void pause()
	{
		if (timeline.getStatus() == Status.RUNNING)
			timeline.stop();
	}

	/**
	 * stop and reset position
	 */
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
