package com.worldventures.wallet.ui.records.detail.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.detail.CardDetailsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(CardDetailsScreenImpl::class), complete = false)
class CardDetailsScreenModule {

   @Provides
   fun provideCardDetailsPresenter(navigator: Navigator,
                                   deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   networkDelegate: WalletNetworkDelegate,
                                   smartCardInteractor: SmartCardInteractor,
                                   analyticsInteractor: WalletAnalyticsInteractor,
                                   recordInteractor: RecordInteractor): CardDetailsPresenter =
         CardDetailsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, networkDelegate,
               recordInteractor, analyticsInteractor)
}
