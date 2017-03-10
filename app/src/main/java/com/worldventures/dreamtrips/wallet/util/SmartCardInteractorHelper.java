package com.worldventures.dreamtrips.wallet.util;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultRecordCommand;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

public class SmartCardInteractorHelper {

   private SmartCardInteractor smartCardInteractor;

   @Inject
   public SmartCardInteractorHelper(SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;
   }

   public void sendSingleDefaultCardTask(Action1<Record> action, @Nullable Observable.Transformer composer) {
      Observable<FetchDefaultRecordCommand> fetchCardObservale = smartCardInteractor.fetchDefaultCardCommandPipe()
            .observeSuccessWithReplay()
            .take(1);
      if (composer != null) fetchCardObservale = fetchCardObservale.compose(composer);
      fetchCardObservale.subscribe(command -> action.call(command.getResult()));
   }
}
