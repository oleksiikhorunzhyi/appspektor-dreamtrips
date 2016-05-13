package com.messenger.di;

import com.messenger.api.ErrorParser;
import com.messenger.delegate.FlagsDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(
        injects = {},
        library = true, complete = false
)
public class FlaggingModule {

    @Provides
    @Singleton
    FlagsDelegate provideFlagsProvider(Janet janet, ErrorParser errorParser) {
        return new FlagsDelegate(janet, errorParser);
    }
}
