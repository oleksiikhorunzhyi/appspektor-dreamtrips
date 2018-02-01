package com.worldventures.wallet.ui.settings.general.profile.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletSettingsProfileScreenImpl::class), complete = false)
class WalletSettingsProfileScreenModule {

   @Provides
   fun provideWalletSettingsProfilePresenter(navigator: Navigator,
                                             deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                             smartCardInteractor: SmartCardInteractor,
                                             analyticsInteractor: WalletAnalyticsInteractor,
                                             smartCardUserDataInteractor: SmartCardUserDataInteractor,
                                             socialInfoProvider: WalletSocialInfoProvider): WalletSettingsProfilePresenter {
      return WalletSettingsProfilePresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor,
            smartCardInteractor, smartCardUserDataInteractor, socialInfoProvider)
   }
}
