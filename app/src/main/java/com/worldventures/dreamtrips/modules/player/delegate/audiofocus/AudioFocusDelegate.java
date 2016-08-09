package com.worldventures.dreamtrips.modules.player.delegate.audiofocus;

import android.content.Context;
import android.media.AudioManager;

import rx.Observable;
import rx.subjects.ReplaySubject;

public class AudioFocusDelegate implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager audioManager;

    private ReplaySubject<Integer> replaySubject;

    public AudioFocusDelegate(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public Observable<Integer> requestFocus() {
        replaySubject = ReplaySubject.create(1);

        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            return Observable.error(new IllegalStateException("Audio focus request failed"));
        } else {
            replaySubject.onNext(AudioManager.AUDIOFOCUS_GAIN);
            return replaySubject.asObservable();
        }
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
        replaySubject.onNext(focusChange);
    }
}
