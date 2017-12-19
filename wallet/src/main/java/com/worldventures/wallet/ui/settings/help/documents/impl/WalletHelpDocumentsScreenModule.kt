package com.worldventures.wallet.ui.settings.help.documents.impl

import com.worldventures.core.modules.infopages.service.DocumentsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletHelpDocumentsScreenImpl::class), complete = false)
class WalletHelpDocumentsScreenModule {

   @Provides
   fun provideWalletHelpDocumentsPresenter(navigator: Navigator,
                                           deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                           documentsInteractor: DocumentsInteractor): WalletHelpDocumentsPresenter =
         WalletHelpDocumentsPresenterImpl(navigator, deviceConnectionDelegate, documentsInteractor)
}
