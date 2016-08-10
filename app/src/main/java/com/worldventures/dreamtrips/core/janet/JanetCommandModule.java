package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.dreamtrips.modules.common.SocialJanetCommandModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlJanetActionsModule;

import dagger.Module;

@Module(includes = {MessengerJanetCommandModule.class,
        DtlJanetActionsModule.class,
        SocialJanetCommandModule.class},
        complete = false, library = true)
public class JanetCommandModule {
}
