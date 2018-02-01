package com.worldventures.wallet.ui.settings.general.impl

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsPresenter
import com.worldventures.wallet.util.WalletFeatureHelper
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletGeneralSettingsScreenImpl::class), complete = false)
class WalletGeneralSettingsScreenModule {

   @Provides
   fun providesWalletGeneralSettingsPresenter(navigator: Navigator,
                                              deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                              smartCardInteractor: SmartCardInteractor,
                                              firmwareInteractor: FirmwareInteractor,
                                              factoryResetInteractor: FactoryResetInteractor,
                                              analyticsInteractor: WalletAnalyticsInteractor,
                                              walletFeatureHelper: WalletFeatureHelper): WalletGeneralSettingsPresenter {
      return WalletGeneralSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
            factoryResetInteractor, analyticsInteractor, walletFeatureHelper)
   }
}
