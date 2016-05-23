package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlJanetActionsModule;

import dagger.Module;

@Module(includes = {MessengerJanetCommandModule.class, DtlJanetActionsModule.class},
        injects = {
                UploaderyImageCommand.class,
                SimpleUploaderyCommand.class,
        },
        complete = false, library = true)
public class JanetCommandModule {
}
