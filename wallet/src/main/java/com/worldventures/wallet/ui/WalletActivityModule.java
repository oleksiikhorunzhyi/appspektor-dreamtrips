package com.worldventures.wallet.ui;

import android.app.Activity;
import android.content.Context;

import com.bluelinelabs.conductor.Router;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardSyncManager;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.WalletCropImageService;
import com.worldventures.wallet.service.WalletCropImageServiceImpl;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.ui.common.activity.WalletActivityPresenter;
import com.worldventures.wallet.ui.common.activity.WalletActivityPresenterImpl;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegateImpl;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegateImpl;
import com.worldventures.wallet.ui.common.navigation.CoreNavigator;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.common.navigation.NavigatorImpl;
import com.worldventures.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

@Module(injects = WalletActivity.class, complete = false, library = true)
public class WalletActivityModule {

   @Singleton
   @Provides
   Router provideRouter(Activity activity) {
      return ((WalletActivity) activity).getRouter();
   }

   @Singleton
   @Provides
   Navigator provideConductorNavigator(Lazy<Router> router, CoreNavigator coreNavigator, WalletBuildConfigHelper walletBuildConfigHelper) {
      return new NavigatorImpl(router, coreNavigator, walletBuildConfigHelper);
   }

   @Provides
   @Singleton
   WalletCropImageService provideWalletCropImageDelegate() {
      return new WalletCropImageServiceImpl();
   }

   @Provides
   WalletNetworkDelegate provideWalletNetworkDelegate(WalletNetworkService walletNetworkService) {
      return new WalletNetworkDelegateImpl(walletNetworkService);
   }

   @Provides
   WalletDeviceConnectionDelegate provideDeviceConnectionDelegate(SmartCardInteractor smartCardInteractor) {
      return new WalletDeviceConnectionDelegateImpl(smartCardInteractor);
   }

   @Provides
   WalletActivityPresenter provideWalletActivityPresenter(SmartCardSyncManager smartCardSyncManager,
         SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler, SmartCardInteractor interactor,
         WalletBluetoothService bluetoothService, AuthInteractor authInteractor) {
      return new WalletActivityPresenterImpl(smartCardSyncManager, smartCardAnalyticErrorHandler,
            interactor, bluetoothService, authInteractor);
   }

   @Provides
   DisableDefaultCardItemProvider provideDisableDefaultCardItemProvider(Context context) {
      return new DisableDefaultCardItemProvider(context);
   }

   @Provides
   AutoClearSmartCardItemProvider provideAutoClearSmartCardItemProvider(Context context) {
      return new AutoClearSmartCardItemProvider(context);
   }
}
