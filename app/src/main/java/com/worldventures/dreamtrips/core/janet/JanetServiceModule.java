package com.worldventures.dreamtrips.core.janet;

import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.modules.background_uploading.service.VideoHttpService;

import dagger.Module;

@Module(
      injects = {
            NewDreamTripsHttpService.class,
            VideoHttpService.class,
      },
      library = true, complete = false)
public class JanetServiceModule {
}
