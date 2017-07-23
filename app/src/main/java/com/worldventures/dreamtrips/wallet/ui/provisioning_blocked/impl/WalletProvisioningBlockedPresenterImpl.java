package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.impl;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.SupportDeviceAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedScreen;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListModel;

import java.util.List;

public class WalletProvisioningBlockedPresenterImpl extends WalletPresenterImpl<WalletProvisioningBlockedScreen> implements WalletProvisioningBlockedPresenter {

   private final AnalyticsInteractor analyticsInteractor;

   public WalletProvisioningBlockedPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(WalletProvisioningBlockedScreen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new SupportDeviceAction()));

      observeSupportedDevices();
      askAllSupportedDevices();
   }

   private void observeSupportedDevices() {
      getSmartCardInteractor().compatibleDevicesActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(
                  ErrorActionStateSubscriberWrapper.<GetCompatibleDevicesCommand>forView(getView().provideOperationDelegate())
                        .onSuccess(action -> {
                           List<Device> response = action.getResult();
                           List<String> devices = Queryable.from(response)
                                 .map(device -> device.manufacturer() + " " + device.model())
                                 .sort()
                                 .toList();
                           getView().onSupportedDevicesLoaded(new SupportedDevicesListModel(devices));
                        })
                        .wrap()
            );
   }

   private void askAllSupportedDevices() {
      getSmartCardInteractor().compatibleDevicesActionPipe().send(new GetCompatibleDevicesCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
