package com.worldventures.wallet.service.command.settings.general.display;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.WalletConstants;
import com.worldventures.wallet.domain.storage.WalletStorage;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.GetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class GetDisplayTypeCommand extends Command<Integer> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletStorage walletStorage;

   private final boolean skipCache;

   public GetDisplayTypeCommand(boolean skipCache) {
      this.skipCache = skipCache;
   }

   @Override
   protected void run(CommandCallback<Integer> callback) throws Throwable {
      if (skipCache) {
         janet.createPipe(GetHomeDisplayTypeAction.class, Schedulers.io())
               .createObservableResult(new GetHomeDisplayTypeAction())
               .map(action -> action.type)
               .doOnNext(displayType -> walletStorage.setSmartCardDisplayType(displayType))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(walletStorage.getSmartCardDisplayType(WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE));
      }
   }
}
