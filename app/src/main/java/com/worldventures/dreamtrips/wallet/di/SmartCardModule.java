package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.PersistentDeviceStorage;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.smartcard.DefaultActionReceiverRoster;
import io.techery.janet.smartcard.DefaultActionSenderRoster;
import io.techery.janet.smartcard.client.NxtSmartCardClient;
import io.techery.janet.smartcard.client.SmartCardClient;
import io.techery.janet.smartcard.mock.client.MockSmartCardClient;
import io.techery.janet.smartcard.receiver.ActionReceiverRoster;
import io.techery.janet.smartcard.sender.ActionSenderRoster;

@Module(complete = false, library = true)
public class SmartCardModule {
    @Singleton
    @Provides
    SmartCardClient provideSmartCardClient(Context context) {
        return new NxtSmartCardClient(context);
    }

    @Singleton
    @Provides
    @Named("MockSmartCardClient")
    SmartCardClient provideMockSmartCardClient(SnappyRepository db) {
        return new MockSmartCardClient(() -> PersistentDeviceStorage.load(db));
    }

    @Singleton
    ActionSenderRoster provideActionSenderRoster() {
        return new DefaultActionSenderRoster();
    }

    @Singleton
    ActionReceiverRoster provideActionReceiverRoster() {
        return new DefaultActionReceiverRoster();
    }
}
