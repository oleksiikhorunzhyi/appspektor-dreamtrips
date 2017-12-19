package com.worldventures.wallet.ui.settings.help.video.impl

import android.content.Context
import com.worldventures.core.modules.video.service.MemberVideosInteractor
import com.worldventures.core.service.CachedEntityDelegate
import com.worldventures.core.service.CachedEntityInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WalletHelpVideoScreenImpl::class), complete = false)
class WalletHelpVideoScreenModule {

   @Provides
   fun provideWalletHelpVideoPresenter(navigator: Navigator,
                                       deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                       memberVideosInteractor: MemberVideosInteractor,
                                       cachedEntityInteractor: CachedEntityInteractor,
                                       cachedEntityDelegate: CachedEntityDelegate, context: Context): WalletHelpVideoPresenter =
         WalletHelpVideoPresenterImpl(navigator, deviceConnectionDelegate, memberVideosInteractor,
               cachedEntityInteractor, cachedEntityDelegate, WalletHelpVideoDelegate(context))
}
