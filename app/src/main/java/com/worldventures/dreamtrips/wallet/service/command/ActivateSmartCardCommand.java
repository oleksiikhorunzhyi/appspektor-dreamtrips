package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.settings.EnableLockUnlockDeviceAction;
import rx.Observable;

@CommandAction
public class ActivateSmartCardCommand extends Command<SmartCard> implements InjectableAction {

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
      smartCardInteractor.activeSmartCardPipe().createObservableResult(new ActiveSmartCardCommand(smartCard))
            .flatMap(command -> smartCardInteractor.fetchCardPropertiesPipe()
                  .createObservableResult(new FetchCardPropertiesCommand()))
            .flatMap(fetchCardPropertiesCommand -> smartCardInteractor.autoClearDelayPipe()
                  .createObservableResult(new SetAutoClearSmartCardDelayCommand(DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS)))
            .doOnNext(smartCard ->
                  janet.createPipe(EnableLockUnlockDeviceAction.class)
                        .createObservableResult(new EnableLockUnlockDeviceAction(true))
                        .onErrorResumeNext(Observable.just(null)))
            .subscribe(action -> callback.onSuccess(smartCard), callback::onFail);
   }
}
