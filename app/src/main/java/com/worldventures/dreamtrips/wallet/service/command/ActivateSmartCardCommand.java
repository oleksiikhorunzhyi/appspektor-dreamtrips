package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class ActivateSmartCardCommand extends Command<SmartCard> implements InjectableAction {

   private final static long DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS = 2 * 60 * 24;

   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand(sc ->
                  ImmutableSmartCard.builder().from(sc).cardStatus(SmartCard.CardStatus.ACTIVE).build()))
            .map(Command::getResult)
            .doOnNext(smartCard -> setDefaultValues())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<SetAutoClearSmartCardDelayCommand> setDefaultValues() {
      return smartCardInteractor.autoClearDelayPipe()
            .createObservableResult(new SetAutoClearSmartCardDelayCommand(DEFAULT_AUTO_CLEAR_DELAY_CLEAR_RECORDS_MINS));
   }
}
