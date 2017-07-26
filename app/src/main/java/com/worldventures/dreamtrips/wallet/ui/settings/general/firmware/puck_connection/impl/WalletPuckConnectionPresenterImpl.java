package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl;


import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionScreen;

import io.techery.janet.helper.ActionStateSubscriber;

public class WalletPuckConnectionPresenterImpl extends WalletPresenterImpl<WalletPuckConnectionScreen> implements WalletPuckConnectionPresenter {

   public WalletPuckConnectionPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService) {
      super(navigator, smartCardInteractor, networkService);
   }

   @Override
   public void attachView(WalletPuckConnectionScreen view) {
      super.attachView(view);
      fetchUserPhoto();
   }

   private void fetchUserPhoto() {
      getSmartCardInteractor().smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> getView().userPhoto(command.getResult().userPhoto()))
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
