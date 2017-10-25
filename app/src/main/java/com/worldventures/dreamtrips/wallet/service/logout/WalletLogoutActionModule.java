package com.worldventures.dreamtrips.wallet.service.logout;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.dreamtrips.wallet.domain.session.NxtSessionHolder;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import java.security.KeyStoreException;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@Module(complete = false, library = true)
public class WalletLogoutActionModule {

   @Provides(type = Provides.Type.SET)
   LogoutAction crypterLogoutAction(HybridAndroidCrypter crypter) {
      return () -> {
         try {
            crypter.deleteKeys();
         } catch (KeyStoreException e) {
            Timber.w(e, "Crypter keys are not cleared");
            throw e;
         }
      };
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction walletSessionActionPipeCreatorLogoutAction(@Named(JANET_WALLET) SessionActionPipeCreator actionPipeCreator) {
      return actionPipeCreator::clearReplays;
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction clearNxtSessionHolderLogoutAction(NxtSessionHolder nxtSessionHolder) {
      return nxtSessionHolder::destroy;
   }
}
