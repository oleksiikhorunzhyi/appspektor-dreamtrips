package com.worldventures.dreamtrips.core.janet;

import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;

import dagger.Module;

@Module(injects = {DreamTripsHttpService.class, NewDreamTripsHttpService.class}, library = true, complete = false)
public class JanetServiceModule {}
