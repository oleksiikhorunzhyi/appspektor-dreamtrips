package com.worldventures.wallet.ui.settings.general.about.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.about.AboutPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(AboutScreenImpl::class), complete = false)
class AboutScreenModule {

   @Provides
   fun provideAboutPresenter(navigator: Navigator,
                             deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                             smartCardInteractor: SmartCardInteractor,
                             recordInteractor: RecordInteractor,
                             analyticsInteractor: WalletAnalyticsInteractor): AboutPresenter =
         AboutPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, recordInteractor, analyticsInteractor)

}
