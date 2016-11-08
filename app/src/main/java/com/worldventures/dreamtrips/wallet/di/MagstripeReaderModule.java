package com.worldventures.dreamtrips.wallet.di;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.magstripe.MagstripeReaderClient;
import io.techery.janet.magstripe.mock.MockClient;
import io.techery.janet.magstripe.rhombus.RhombusClient;

@Module(complete = false, library = true)
public class MagstripeReaderModule {
    @Singleton
    @Provides
    MagstripeReaderClient provideSmartCardClient() {
        return new RhombusClient.Builder().build();
    }

    @Singleton
    @Provides
    @Named("Mock")
    MagstripeReaderClient provideMockSmartCardClient() {
        return new MockClient.Builder()
              .swipeDelay(200)
              .swipeRepeatCount(1)
              .build();
    }
}
