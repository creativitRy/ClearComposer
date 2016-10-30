/*
 * MIT License
 *
 * Copyright (c) 2016 Gahwon "creativitRy" Lee
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
 * @author creativitRy
 * Date: 10/29/2016.
 */
package com.ctry.clearcomposer.music;

import com.ctry.clearcomposer.ClearComposer;
import com.ctry.clearcomposer.sequencer.GraphicTrack;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TrackPlayer
{
	private int index;
	private List<GraphicTrack> tracks;
	private Timeline timeline;

	private static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... params)
	{
		try {
			return type.getConstructor(params);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * constructs a new track player with multiple blank tracks
	 */
	public TrackPlayer()
	{
		index = 0;
		timeline = new Timeline(new KeyFrame(Duration.millis(ClearComposer.constants.getTempo() ), ae -> playNotes()));
		timeline.setCycleCount(Animation.INDEFINITE);

		tracks = new ArrayList<>();

		//TODO
		play();
	}

	/**
	 * Saves track data to an byte stream.
	 * Note that this will not close the file.
	 * @param out byte stream to write to.
	 * @throws IOException when an I/O error occurs while writing
	 */
	public void saveTrack(OutputStream out) throws IOException
	{
		//TODO: constants;
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeInt(tracks.size());
		for (GraphicTrack track : tracks)
		{
			oos.writeObject(track.getClass());
			oos.writeObject(track.getColor());
			track.saveTrackData(oos);
		}
	}

	/**
	 * Loads track data from an byte stream.
	 * Note that this will not close the file.
	 * @param in byte stream to write to.
	 * @throws IOException when an I/O error occurs while writing
	 */
	public void loadTrack(InputStream in) throws IOException
	{
		//TODO: constants
		ObjectInputStream ois = new ObjectInputStream(in);
		int trackNum = ois.readInt();


		List<GraphicTrack> tracksLoad = new ArrayList<>();
		for (int i = 0; i < trackNum; i++)
		{
			try {
				GraphicTrack track;
				Class<?> trackClass = (Class<?>) ois.readObject();
				if (!GraphicTrack.class.isAssignableFrom(trackClass))
					throw new IOException("Track not instanceof GraphicsTrack");
				Color color = (Color) ois.readObject();

				Constructor<?> defCon = getConstructor(trackClass);
				Constructor<?> colorCon = getConstructor(trackClass, Color.class);
				if (colorCon != null)
					track = (GraphicTrack)colorCon.newInstance(color);
				else if (defCon != null)
					track = (GraphicTrack)defCon.newInstance();
				else
					throw new IOException("No suitable constructor found for track");
				track.loadTrackData(ois);
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
				throw new IOException(e);
			}
		}

		tracks.clear();
		tracks.addAll(tracksLoad);
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
		timeline.setDelay(Duration.millis(ClearComposer.constants.getTempo() ));
	}

	/**
	 * play
	 */
	public void play()
	{
		timeline.play();
	}

	/**
	 * stop at current position
	 */
	public void pause()
	{
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
