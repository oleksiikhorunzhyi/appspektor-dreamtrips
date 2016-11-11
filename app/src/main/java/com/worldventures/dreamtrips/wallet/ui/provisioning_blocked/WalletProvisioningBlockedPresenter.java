package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.util.List;

import javax.inject.Inject;

public class WalletProvisioningBlockedPresenter extends WalletPresenter<WalletProvisioningBlockedPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;
   @Inject FeatureManager featureManager;

   public WalletProvisioningBlockedPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

   }

   public interface Screen extends WalletScreen {
      void onSupportedDevicesLoaded(List<String> devices);
   }
}
