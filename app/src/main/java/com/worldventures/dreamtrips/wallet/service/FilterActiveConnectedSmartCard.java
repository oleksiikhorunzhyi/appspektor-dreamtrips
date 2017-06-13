package com.worldventures.dreamtrips.wallet.service;


import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import rx.Observable;

import static com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus.CONNECTED;

public final class FilterActiveConnectedSmartCard implements Observable.Transformer<Object, SmartCard> {

   private final SmartCardInteractor interactor;


   public FilterActiveConnectedSmartCard(SmartCardInteractor interactor) {this.interactor = interactor;}

   @Override
   public Observable<SmartCard> call(Observable<Object> target) {
      return target.flatMap(value ->
            Observable.zip(
                  interactor.activeSmartCardPipe()
                        .createObservableResult(new ActiveSmartCardCommand()),
                  interactor.deviceStatePipe()
                        .createObservableResult(DeviceStateCommand.fetch()),
                  (activeCommand, cardStateCommand) -> new Pair<>(activeCommand.getResult(), cardStateCommand.getResult()))
                  .filter(pair -> pair.second.connectionStatus() == CONNECTED
                        && pair.first.cardStatus() == SmartCard.CardStatus.ACTIVE)
                  .map(pair -> pair.first));
   }
}