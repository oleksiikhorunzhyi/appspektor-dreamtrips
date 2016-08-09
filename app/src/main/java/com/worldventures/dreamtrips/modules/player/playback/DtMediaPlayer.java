package com.worldventures.dreamtrips.modules.player.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class DtMediaPlayer implements DtPlayer {

    private Context context;

    private MediaPlayer mediaPlayer;
    private State state;
    private PublishSubject<State> stateObservable = PublishSubject.create();
    private Uri uri;

    public DtMediaPlayer(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
        setState(State.UNKNOWN);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void start() {
        if (state == State.PREPARING || state == State.PLAYING) {
            return;
        }
        if (state == State.PAUSED || state == State.READY) {
            mediaPlayer.start();
            return;
        }
        try {
            Timber.d("Podcasts -- DtMediaPlayer -- prepare player, state %s", state);
            prepareAndPlay();
        } catch (Exception ex) {
            ex.printStackTrace();
            Timber.e(ex, "Could not initialize player");
            setState(State.ERROR);
        }
    }

    private void prepareAndPlay() throws Exception {
        setState(State.PREPARING);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(context, uri);
        
        mediaPlayer.prepare();
        mediaPlayer.start();
        setState(State.PLAYING);
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        setState(State.PAUSED);
    }

    @Override
    public void release() {
        Timber.d("Podcasts -- DtMediaPlayer -- Release player");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            setState(State.STOPPED);
        }
    }

    private void setState(State state) {
        this.state = state;
        Timber.d("Podcasts -- DtMediaPlayer -- post state %s", state);
        stateObservable.onNext(state);
    }

    @Override
    public Uri getSourceUri() {
        return uri;
    }

    @Override
    public Observable<State> getStateObservable() {
        return stateObservable;
    }

    @Override
    public MediaController.MediaPlayerControl getMediaPlayerControl() {
        return new MediaPlayerControl();
    }

    private class MediaPlayerControl implements MediaController.MediaPlayerControl {

        public void start() {
            mediaPlayer.start();
        }

        public void pause() {
            mediaPlayer.pause();
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public void seekTo(int i) {
            mediaPlayer.seekTo(i);
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }

        public int getBufferPercentage() {
            return 0;
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    }
}
