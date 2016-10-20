package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import javax.inject.Inject;

import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

   @Inject SmartCardInteractor interactor;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      interactor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .flatMap(command -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(command.getResult())))
            .subscribe(connectAction -> Timber.i("Success connection to smart card"), throwable -> {
            });
   }
}
