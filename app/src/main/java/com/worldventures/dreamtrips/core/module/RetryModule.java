package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.modules.dtl.store.RetryLoginComposer;

import dagger.Module;

@Module(
        injects = {
                RetryLoginComposer.class},
        library = true, complete = false
)
public class RetryModule {
}
