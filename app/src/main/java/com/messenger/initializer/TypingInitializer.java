package com.messenger.initializer;

import com.messenger.delegate.chat.typing.TypingManager;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;

import javax.inject.Inject;

public class TypingInitializer implements AppInitializer {
    @Inject TypingManager typingInteractor;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        typingInteractor.clearCache();
    }
}
