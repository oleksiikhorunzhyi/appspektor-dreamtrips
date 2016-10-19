package com.worldventures.dreamtrips.wallet.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.magstripe.action.StartRecordingAction;
import io.techery.janet.magstripe.action.StopRecordingAction;
import io.techery.janet.magstripe.action.SwipeCardEventAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Singleton
public final class MagstripeReaderInteractor {

    private final ActionPipe<StartRecordingAction> startRecordingActionPipe;
    private final ActionPipe<StopRecordingAction> stopRecordingActionPipe;
    private final ReadActionPipe<SwipeCardEventAction> swipeCardEventActionPipe;

    @Inject
    public MagstripeReaderInteractor(@Named(JANET_WALLET) Janet janet) {
        startRecordingActionPipe = janet.createPipe(StartRecordingAction.class);
        stopRecordingActionPipe = janet.createPipe(StopRecordingAction.class);
        swipeCardEventActionPipe = janet.createPipe(SwipeCardEventAction.class).asReadOnly();
    }

    public ActionPipe<StartRecordingAction> startRecordingActionPipe() {
        return startRecordingActionPipe;
    }

    public ActionPipe<StopRecordingAction> stopRecordingActionPipe() {
        return stopRecordingActionPipe;
    }

    public ReadActionPipe<SwipeCardEventAction> swipeCardEventActionPipe() {
        return swipeCardEventActionPipe;
    }
}
