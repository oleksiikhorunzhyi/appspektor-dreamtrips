package com.worldventures.wallet.ui.settings.general.firmware.puck_connection.impl;

import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionScreen;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletPuckConnectionPresenterImpl extends WalletPresenterImpl<WalletPuckConnectionScreen> implements WalletPuckConnectionPresenter {

   private final SmartCardInteractor smartCardInteractor;

   public WalletPuckConnectionPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
   }

   @Override
   public void attachView(WalletPuckConnectionScreen view) {
      super.attachView(view);
      fetchUserPhoto();
   }

   private void fetchUserPhoto() {
      smartCardInteractor.smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> {
                     if (command.getResult() != null) {
                        getView().userPhoto(command.getResult().userPhoto());
                     }
                  })
            );
   }

   @Override
   public void goNext() {
      getNavigator().goFirmwareDownload();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
