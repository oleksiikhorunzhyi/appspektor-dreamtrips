package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                CompressImageForSmartCardCommand.class
        },
        complete = false, library = true
)
public class WalletCommandModule {
}
