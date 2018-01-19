package com.worldventures.wallet.ui.settings.security.lostcard.impl

import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.settings.security.lostcard.MapPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(MapScreenImpl::class), complete = false)
class MapScreenModule {

   @Provides
   fun provideMapPresenter(smartCardLocationInteractor: SmartCardLocationInteractor,
                           analyticsInteractor: WalletAnalyticsInteractor): MapPresenter =
         MapPresenterImpl(smartCardLocationInteractor, analyticsInteractor)
}
