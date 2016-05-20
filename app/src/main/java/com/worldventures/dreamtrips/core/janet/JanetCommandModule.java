package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyImageCommand;
import com.worldventures.dreamtrips.modules.dtl.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlUpdateAmenitiesAction;

import dagger.Module;

@Module(includes = {MessengerJanetCommandModule.class},
        injects = {
                UploaderyImageCommand.class,
                SimpleUploaderyCommand.class,
                DtlUpdateAmenitiesAction.class,
                DtlFilterDataAction.class
        },
        complete = false, library = true)
public class JanetCommandModule {
}
