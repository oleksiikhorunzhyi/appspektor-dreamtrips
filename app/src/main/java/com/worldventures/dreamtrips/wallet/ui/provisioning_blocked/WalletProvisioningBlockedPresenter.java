package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import android.content.Context;
import android.os.Parcelable;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.SupportDeviceAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListModel;

import java.util.List;

import javax.inject.Inject;

public class WalletProvisioningBlockedPresenter extends WalletPresenter<WalletProvisioningBlockedPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   public WalletProvisioningBlockedPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new SupportDeviceAction()));

      observeSupportedDevices();
      askAllSupportedDevices();
   }

   private void observeSupportedDevices() {
      smartCardInteractor.compatibleDevicesActionPipe()
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
      smartCardInteractor.compatibleDevicesActionPipe().send(new GetCompatibleDevicesCommand());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void onSupportedDevicesLoaded(SupportedDevicesListModel devices);

   }
}
