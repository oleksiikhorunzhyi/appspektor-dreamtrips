package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
                //TODO: put presenters here
        },
        complete = false, library = true
)
public class WalletActivityModule {
    public static final String WALLET = "Wallet";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(WALLET, R.string.messenger, R.string.messenger, R.drawable.ic_messenger,
                true, null);
    }
}
