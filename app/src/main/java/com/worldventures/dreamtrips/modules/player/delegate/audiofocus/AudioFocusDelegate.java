package com.worldventures.dreamtrips.modules.player.delegate.audiofocus;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.subjects.ReplaySubject;

public class AudioFocusDelegate implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager audioManager;

    private ReplaySubject<AudioFocusState> replaySubject;

    public AudioFocusDelegate(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public Observable<AudioFocusState> requestFocus() {
        replaySubject = ReplaySubject.create(1);

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            replaySubject.onNext(AudioFocusState.FAILED);
        } else {
            replaySubject.onNext(AudioFocusState.GAINED);
        }

        return replaySubject.asObservable();
    }

    /**
     * Pay attention that requestFocus() must be called before calling this method.
     * @return audio focus observable corresponding to previous requestFocus() call,
     * null if audio focus was abandoned or requstFocus() was never called.
     */
    @Nullable public Observable<AudioFocusState> getAudioFocusObservable() {
        return replaySubject;
    }

    public void abandonFocus() {
        if (replaySubject != null) {
            replaySubject.onCompleted();
            replaySubject = null;
        }
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                replaySubject.onNext(AudioFocusState.GAINED);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                replaySubject.onNext(AudioFocusState.LOSS);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                replaySubject.onNext(AudioFocusState.LOSS_TRANSIENT);
                break;
        }
    }

    public enum AudioFocusState {
        GAINED, FAILED, LOSS, LOSS_TRANSIENT
    }
}
