package com.techery.spares.module;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.techery.spares.utils.delegate.ScreenChangedEventDelegate;
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate;
import com.techery.spares.utils.delegate.StoryLikedEventDelegate;

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

    @Provides
    @Singleton
    ScreenChangedEventDelegate provideScreenChangedDelegate() {
        return new ScreenChangedEventDelegate();
    }

    @Provides
    @Singleton
    StoryLikedEventDelegate provideStoryLikedEventDelegate() {
        return new StoryLikedEventDelegate();
    }

    @Provides
    @Singleton
    NotificationCountEventDelegate provideNotificationCountDelegate() {
        return new NotificationCountEventDelegate();
    }
}
