package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                CompressImageForSmartCardCommand.class,
                LoadImageForSmartCardCommand.class,
                CreateAndConnectToCardCommand.class
        },
        complete = false, library = true
)
public class WalletCommandModule {
}
