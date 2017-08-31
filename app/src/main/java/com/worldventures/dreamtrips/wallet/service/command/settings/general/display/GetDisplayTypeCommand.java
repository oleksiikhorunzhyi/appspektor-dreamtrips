package com.worldventures.dreamtrips.wallet.service.command.settings.general.display;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.WalletConstants;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.GetHomeDisplayTypeAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class GetDisplayTypeCommand extends Command<Integer> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;

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
               .doOnNext(displayType -> snappyRepository.setSmartCardDisplayType(displayType))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         callback.onSuccess(snappyRepository.getSmartCardDisplayType(WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE));
      }
   }
}
