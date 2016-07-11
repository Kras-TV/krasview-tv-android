package com.example.anya.videoexample;

import android.media.MediaPlayer;

import java.util.Map;

public interface VideoInterface {
	public void setVideoAndStart(String address);
	public void stop();
	public void pause();
	public void play();
	public void setTVController(Object tc);
	public void setVideoController(Object vc);
	public void setMap(Map<String, Object> map);
	public boolean isPlaying();
	public boolean showOverlay();
	public boolean hideOverlay();
	public int getProgress();
	public int getLeight();
	public int getTime();
	public void setTime(int time);
	public int changeSizeMode();
	public String changeAudio();
	public String changeSubtitle();
	public int getAudioTracksCount();
	public int getSpuTracksCount();
	public void setOnCompletionListener( MediaPlayer.OnCompletionListener listener);
	public void setOnErrorListener (MediaPlayer.OnErrorListener l);

	public int changeOrientation();
	public void end();
}
