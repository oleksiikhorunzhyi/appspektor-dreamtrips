package com.worldventures.dreamtrips.modules.player;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;
import com.worldventures.dreamtrips.modules.player.delegate.audiofocus.AudioFocusDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        library = true, complete = false
)
public class PodcastAppModule {
    @Provides
    @Singleton
    PodcastPlayerDelegate providePodcastPlayerDelegate(@ForApplication Context context) {
        return new PodcastPlayerDelegate(context);
    }

    @Provides
    @Singleton
    AudioFocusDelegate provideAudioFocuesDelegate(@ForApplication Context context) {
        return new AudioFocusDelegate(context);
    }
}
