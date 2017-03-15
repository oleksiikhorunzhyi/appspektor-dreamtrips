package com.worldventures.dreamtrips.wallet.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

public class SmartCardInteractorHelper {

   private SmartCardInteractor smartCardInteractor;

   @Inject
   public SmartCardInteractorHelper(SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;
   }

   public void sendSingleDefaultCardTask(Action1<Record> action, Observable.Transformer composer) {
      smartCardInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .flatMap(command -> smartCardInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.fetchById(command.getResult()))
                  .flatMap(recordsCommand -> Observable.just(
                        Queryable.from(recordsCommand.getResult()).first())))
            .compose(composer)
            .subscribe(action);
   }
}
