package com.worldventures.dreamtrips.modules.player;

import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerDelegate;

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
    PodcastPlayerDelegate providePodcastPlayerDelegate() {
        return new PodcastPlayerDelegate();
    }
}
