package com.worldventures.wallet.ui.dashboard.impl

import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.lostcard.LocationTrackingManager
import com.worldventures.wallet.ui.common.WalletNavigationDelegate
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.dashboard.CardListPresenter
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateModule
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegateFactory
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType
import com.worldventures.wallet.util.WalletFeatureHelper
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(CardListScreenImpl::class),
      includes = arrayOf(FactoryResetDelegateModule::class), complete = false)
class CardListScreenModule {

   @Provides
   fun provideCardListPresenter(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                networkDelegate: WalletNetworkDelegate, smartCardInteractor: SmartCardInteractor,
                                recordInteractor: RecordInteractor, firmwareInteractor: FirmwareInteractor,
                                analyticsInteractor: WalletAnalyticsInteractor, factoryResetDelegateFactory: FactoryResetDelegateFactory,
                                navigationDelegate: WalletNavigationDelegate, walletFeatureHelper: WalletFeatureHelper,
                                locationTrackingManager: LocationTrackingManager): CardListPresenter {
      return CardListPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate,
            smartCardInteractor, recordInteractor,
            firmwareInteractor, analyticsInteractor, factoryResetDelegateFactory.createFactoryResetDelegate(FactoryResetType.DASHBOARD),
            navigationDelegate, walletFeatureHelper, locationTrackingManager)
   }
}
