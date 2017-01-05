package com.worldventures.dreamtrips.core.janet;

import com.messenger.di.MessengerJanetCommandModule;
import com.worldventures.dreamtrips.modules.common.SocialJanetCommandModule;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlJanetCommandModule;
import com.worldventures.dreamtrips.wallet.di.WalletCommandModule;

import dagger.Module;

@Module(includes = {
      MessengerJanetCommandModule.class,
      DtlJanetCommandModule.class,
      WalletCommandModule.class,
      SocialJanetCommandModule.class,
},
        complete = false, library = true)
class JanetCommandModule {
}
