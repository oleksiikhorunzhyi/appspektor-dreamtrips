package com.worldventures.wallet.ui.start.impl

import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAccessValidator
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.start.WalletStartPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletStartScreenImpl::class), complete = false)
class WalletStartScreenModule {

   @Provides
   fun provideWalletStartPresenter(navigator: Navigator,
                                   deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   smartCardInteractor: SmartCardInteractor,
                                   firmwareInteractor: FirmwareInteractor,
                                   walletAccessValidator: WalletAccessValidator,
                                   httpErrorHandlingUtil: HttpErrorHandlingUtil): WalletStartPresenter =
         WalletStartPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
               walletAccessValidator, httpErrorHandlingUtil)

}
