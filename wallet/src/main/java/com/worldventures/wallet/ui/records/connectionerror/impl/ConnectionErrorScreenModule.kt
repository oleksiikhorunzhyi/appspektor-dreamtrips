package com.worldventures.wallet.ui.records.connectionerror.impl

import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.connectionerror.ConnectionErrorPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(ConnectionErrorScreenImpl::class), complete = false)
class ConnectionErrorScreenModule {

   @Provides
   fun provideConnectionErrorPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       smartCardInteractor: SmartCardInteractor): ConnectionErrorPresenter =
         ConnectionErrorPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor)

}
