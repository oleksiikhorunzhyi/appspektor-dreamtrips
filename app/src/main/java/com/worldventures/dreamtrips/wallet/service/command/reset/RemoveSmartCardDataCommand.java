package com.worldventures.dreamtrips.wallet.service.command.reset;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class RemoveSmartCardDataCommand extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;
   @Inject CardListStorage cardListStorage;
   @Inject LostCardRepository lostCardRepository;
   @Inject SmartCardInteractor smartCardInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .flatMap(command -> removeCache())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> removeCache() {
      return Observable.create(subscriber -> {
         snappyRepository.deleteWalletDefaultCardId();
         cardListStorage.deleteWalletCardList();
         snappyRepository.deleteSmartCardDetails();
         snappyRepository.deleteSmartCard();
         snappyRepository.deleteTermsAndConditions();
         lostCardRepository.clear();
         subscriber.onNext(null);
         subscriber.onCompleted();
      });
   }

}
