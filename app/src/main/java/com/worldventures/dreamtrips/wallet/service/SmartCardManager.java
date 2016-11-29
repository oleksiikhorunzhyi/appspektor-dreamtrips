package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardModifier;

import io.techery.janet.Command;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class SmartCardManager {

   private final BehaviorSubject<SmartCard> subject = BehaviorSubject.create();
   private final SmartCardInteractor smartCardInteractor;

   public SmartCardManager(SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;

      Observable.merge(
            smartCardInteractor.smartCardModifierPipe().observeSuccess().map(SmartCardModifier::getResult),
            smartCardInteractor.activeSmartCardPipe().observeSuccess().map(Command::getResult)
      ).subscribe(subject::onNext);
   }

   public Observable<SmartCard> smartCardObservable() {
      if (subject.hasValue()) {
         smartCardInteractor.activeSmartCardPipe().send(new GetActiveSmartCardCommand());
      }
      return subject.asObservable();
   }

   public Observable<SmartCard> singleSmartCardObservable() {
      return smartCardObservable().take(1);
   }
}
