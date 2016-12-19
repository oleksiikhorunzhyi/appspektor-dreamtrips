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

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.SetClearRecordsDelayAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import rx.Observable;

@CommandAction
public class ActivateSmartCardCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier, CachedAction<SmartCard> {

   private final static long DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS = 2 * 60 * 24;

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

      janet.createPipe(UpdateSmartCardPropertiesCommand.class)
            .createObservableResult(new UpdateSmartCardPropertiesCommand())
            .map(UpdateSmartCardPropertiesCommand::getResult)
            .flatMap(this::setDefaults)
            .doOnNext(smartCard ->
                  janet.createPipe(EnableLockUnlockDeviceAction.class)
                        .createObservableResult(new EnableLockUnlockDeviceAction(true))
                        .onErrorResumeNext(Observable.just(null)))
            .subscribe(action -> callback.onSuccess(smartCard), throwable -> callback.onFail(throwable));
   }

   private Observable<SmartCard> setDefaults(SmartCard smartCard) {
      return janet.createPipe(SetClearRecordsDelayAction.class)
            .createObservableResult(new SetClearRecordsDelayAction(TimeUnit.MINUTES, DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS))
            .map(action -> ImmutableSmartCard.copyOf(smartCard)
                  .withClearFlyeDelay(DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS));
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
