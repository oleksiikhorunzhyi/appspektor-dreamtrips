package com.worldventures.wallet.ui.wizard.records.sync.impl

import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.records.sync.SyncRecordsPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(SyncRecordsScreenImpl::class), complete = false)
class SyncRecordsScreenModule {

   @Provides
   fun provideSyncRecordsPresenter(navigator: Navigator,
                                   deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                   smartCardInteractor: SmartCardInteractor,
                                   recordInteractor: RecordInteractor,
                                   analyticsInteractor: WalletAnalyticsInteractor): SyncRecordsPresenter =
         SyncRecordsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, recordInteractor, analyticsInteractor)

}
