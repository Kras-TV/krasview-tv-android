package ru.krasview.tv.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceView;
import org.videolan1.libvlc.IVideoPlayer;

import java.util.Map;

/**
 * Created by anya on 25.06.16.
 */
public class VideoViewVLC extends SurfaceView implements IVideoPlayer, VideoInterface {
    public VideoViewVLC(Context context) {
        super(context);
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {

    }

    @Override
    public void setVideoAndStart(String address) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void play() {

    }

    @Override
    public void setTVController(TVController tc) {

    }

    @Override
    public void setVideoController(VideoController vc) {

    }

    @Override
    public void setMap(Map<String, Object> map) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean showOverlay() {
        return false;
    }

    @Override
    public boolean hideOverlay() {
        return false;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getLeight() {
        return 0;
    }

    @Override
    public int getTime() {
        return 0;
    }

    @Override
    public void setTime(int time) {

    }

    @Override
    public int changeSizeMode() {
        return 0;
    }

    @Override
    public String changeAudio() {
        return null;
    }

    @Override
    public String changeSubtitle() {
        return null;
    }

    @Override
    public int getAudioTracksCount() {
        return 0;
    }

    @Override
    public int getSpuTracksCount() {
        return 0;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {

    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {

    }

    @Override
    public int changeOrientation() {
        return 0;
    }

    @Override
    public void end() {

    }
}
