package ru.krasview.tv.player;

import java.util.Map;

import android.media.MediaPlayer;

public interface VideoInterface {
	public void setVideoAndStart(String address);
	public void stop();
	public void pause();
	public void play();
	public void setTVController(TVController tc);
	public void setVideoController(VideoController vc);
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
