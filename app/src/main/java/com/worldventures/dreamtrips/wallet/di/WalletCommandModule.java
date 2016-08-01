package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;

import dagger.Module;

@Module(
        includes = {
        },
        injects = {
                CompressImageForSmartCardCommand.class,
                CardListCommand.class,
                AttachCardCommand.class,
                CardStacksCommand.class,
                LoadImageForSmartCardCommand.class,
                CreateAndConnectToCardCommand.class
        },
        complete = false, library = true
)
public class WalletCommandModule {
}

