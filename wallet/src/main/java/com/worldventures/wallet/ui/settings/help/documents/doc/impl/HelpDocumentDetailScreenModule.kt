package com.worldventures.wallet.ui.settings.help.documents.doc.impl

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(HelpDocumentDetailScreenImpl::class), complete = false)
class HelpDocumentDetailScreenModule {

   @Provides
   fun provideHelpDocumentDetailPresenter(navigator: Navigator, deviceConnectionDelegate: WalletDeviceConnectionDelegate):
         HelpDocumentDetailPresenter = HelpDocumentDetailPresenterImpl(navigator, deviceConnectionDelegate)
}
