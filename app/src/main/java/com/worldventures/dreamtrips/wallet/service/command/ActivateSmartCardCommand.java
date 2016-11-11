package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import rx.Observable;

@CommandAction
public class ActivateSmartCardCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   @Inject SnappyRepository snappyRepository;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SmartCardInteractor smartCardInteractor;

   private SmartCard smartCard;

   public ActivateSmartCardCommand(SmartCard smartCard) {
      this.smartCard = smartCard;
   }

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      smartCard = ImmutableSmartCard.builder()
            .from(this.smartCard)
            .cardStatus(SmartCard.CardStatus.ACTIVE)
            .build();
      snappyRepository.setActiveSmartCardId(smartCard.smartCardId());

      smartCardInteractor.enableLockUnlockDeviceActionPipe()
            .createObservableResult(new EnableLockUnlockDeviceAction(true))
            .onErrorResumeNext(Observable.just(null))
            .subscribe(action -> callback.onSuccess(smartCard), throwable -> callback.onFail(throwable));
   }

   @Override
   public SmartCard getCacheData() {
      return smartCard;
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCard cache) {
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .restoreFromCache(false)
            .saveToCache(true)
            .build();
   }
}
