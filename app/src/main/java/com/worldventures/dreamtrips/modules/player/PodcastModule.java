package com.worldventures.dreamtrips.modules.player;

import com.worldventures.dreamtrips.modules.player.presenter.PodcastPresenterImpl;
import com.worldventures.dreamtrips.modules.player.view.PodcastPlayerScreenImpl;

import dagger.Module;

@Module(
      injects = {PodcastPresenterImpl.class, PodcastPlayerScreenImpl.class},
      complete = false,
      library = true)
public class PodcastModule {}
