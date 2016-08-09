package com.worldventures.dreamtrips.modules.player.delegate.audiofocus;

import android.media.AudioManager;

import rx.Subscriber;
import rx.functions.Action0;

public class AudioFocusSubscriber extends Subscriber<Integer> {

    private Action0 onGain;
    private Action0 onLoss;
    private Action0 onLossTransient;
    private Action0 onLossTransientCanDuck;
    private Action0 onError;

    public AudioFocusSubscriber onError(Action0 onError) {
        this.onError = onError;
        return this;
    }

    public AudioFocusSubscriber onGain(Action0 onGain) {
        this.onGain = onGain;
        return this;
    }

    public AudioFocusSubscriber onLoss(Action0 onLoss) {
        this.onLoss = onLoss;
        return this;
    }

    public AudioFocusSubscriber onLossTransient(Action0 onLossTransient) {
        this.onLossTransient = onLossTransient;
        return this;
    }

    public AudioFocusSubscriber onLossTransientCanDuck(Action0 onLossTransientCanDuck) {
        this.onLossTransientCanDuck = onLossTransientCanDuck;
        return this;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        if (onError != null) onError.call();
    }

    @Override
    public void onNext(Integer focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (onGain != null) onGain.call();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (onLoss != null) onLoss.call();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (onLossTransient != null) onLossTransient.call();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (onLossTransientCanDuck != null) onLossTransientCanDuck.call();
                break;
        }
    }
}
