package com.worldventures.wallet.ui.settings.security.lostcard.impl

import android.app.Activity
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.location.WalletDetectLocationService
import com.worldventures.wallet.ui.common.LocationScreenComponent
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(LostCardScreenImpl::class), complete = false)
class LostCardScreenModule {

   @Provides
   @Suppress("UnsafeCast")
   fun provideLostCardPresenter(navigator: Navigator,
                                deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                permissionDispatcher: PermissionDispatcher,
                                smartCardLocationInteractor: SmartCardLocationInteractor,
                                walletDetectLocationService: WalletDetectLocationService,
                                activity: Activity,
                                analyticsInteractor: WalletAnalyticsInteractor): LostCardPresenter =
         LostCardPresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher,
               smartCardLocationInteractor, walletDetectLocationService,
               activity.getSystemService(LocationScreenComponent.COMPONENT_NAME) as LocationScreenComponent, analyticsInteractor)
}
