package com.worldventures.dreamtrips.core.janet;

import com.worldventures.core.service.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.VideoHttpService;

import dagger.Module;

@Module(
      injects = {
            NewDreamTripsHttpService.class,
            VideoHttpService.class,
      },
      library = true, complete = false)
public class JanetServiceModule {
}
