package com.techery.spares.module;

import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class EventDelegateModule {

    @Provides
    @Singleton
    SearchFocusChangedDelegate provideSearchFocusChangedDelegate() {
        return new SearchFocusChangedDelegate();
    }
}
