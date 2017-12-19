package com.worldventures.wallet.ui.settings.general.display.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(DisplayOptionsSettingsScreenImpl::class), complete = false)
class DisplayOptionsSettingsScreenModule {

   @Provides
   fun provideDisplayOptionsSettingsPresenter(navigator: Navigator,
                                              deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                              smartCardInteractor: SmartCardInteractor,
                                              smartCardUserDataInteractor: SmartCardUserDataInteractor,
                                              analyticsInteractor: WalletAnalyticsInteractor,
                                              socialInfoProvider: WalletSocialInfoProvider): DisplayOptionsSettingsPresenter =
         DisplayOptionsSettingsPresenterImpl(navigator, deviceConnectionDelegate,
               WalletProfileDelegate(smartCardUserDataInteractor, smartCardInteractor, analyticsInteractor),
               smartCardInteractor, socialInfoProvider)
}
