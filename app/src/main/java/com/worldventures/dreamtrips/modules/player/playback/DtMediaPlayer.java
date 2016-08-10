package com.worldventures.dreamtrips.modules.player.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.webkit.URLUtil;

import rx.Observable;
import rx.subjects.ReplaySubject;
import timber.log.Timber;

public class DtMediaPlayer implements DtPlayer {

    private Context context;

    private MediaPlayer mediaPlayer;
    private State state;
    private ReplaySubject<State> stateObservable = ReplaySubject.create(1);
    private Uri uri;

    public DtMediaPlayer(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
        setState(State.UNKNOWN);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void prepare() {
        if (state != State.UNKNOWN) {
            return;
        }
        try {
            Timber.d("Podcasts -- DtMediaPlayer -- prepare player, state %s", state);
            tryPrepare();
        } catch (Exception ex) {
            Timber.e(ex, "Could not prepare player");
            setState(State.ERROR);
        }
    }

    private void tryPrepare() throws Exception {
        setState(State.PREPARING);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // check if URL to avoid no content provider error in media player
        String possibleUrl = uri.toString();
        if (URLUtil.isValidUrl(possibleUrl)) {
            mediaPlayer.setDataSource(possibleUrl);
        } else {
            mediaPlayer.setDataSource(context, uri);
        }
        mediaPlayer.prepare();
        setState(State.READY);
    }

    @Override
    public void start() {
        if (state == State.PREPARING || state == State.PLAYING || state == State.ERROR) {
            return;
        }
        if (state == State.PAUSED || state == State.READY) {
            mediaPlayer.start();
            return;
        }
        setState(State.ERROR);
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
            mediaPlayer.stop();
            mediaPlayer.release();
            setState(State.STOPPED);
        }
    }

    private void setState(State state) {
        this.state = state;
        Timber.d("Podcasts -- DtMediaPlayer -- post state %s", state);
        stateObservable.onNext(state);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public State getState() {
        return state;
    }

    @Override
    public Observable<State> getStateObservable() {
        return stateObservable;
    }

    @Override
    public Uri getSourceUri() {
        return uri;
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
}
