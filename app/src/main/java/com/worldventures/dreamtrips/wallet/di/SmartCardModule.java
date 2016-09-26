package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.PersistentDeviceStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;
import io.techery.janet.smartcard.DefaultActionReceiverRoster;
import io.techery.janet.smartcard.DefaultActionSenderRoster;
import io.techery.janet.smartcard.client.NxtSmartCardClient;
import io.techery.janet.smartcard.client.SmartCardClient;
import io.techery.janet.smartcard.event.receiver.ActionReceiverRoster;
import io.techery.janet.smartcard.mock.client.MockSmartCardClient;
import io.techery.janet.smartcard.sender.ActionSenderRoster;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

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

   @Singleton
   @Provides
   WizardInteractor provideWizardInteractor(@Named(JANET_WALLET) Janet janet) {
      return new WizardInteractor(janet);
   }

   @Singleton
   @Provides
   FirmwareInteractor firmwareInteractor(@Named(JANET_WALLET) Janet janet) {
      return new FirmwareInteractor(janet);
   }
}
