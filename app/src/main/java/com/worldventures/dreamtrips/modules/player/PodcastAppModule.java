package com.worldventures.dreamtrips.modules.player;

import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayer;
import com.worldventures.dreamtrips.modules.player.delegate.PodcastPlayerImpl;

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
    PodcastPlayer providePodcastPlayer() {
        return new PodcastPlayerImpl();
    }
}
