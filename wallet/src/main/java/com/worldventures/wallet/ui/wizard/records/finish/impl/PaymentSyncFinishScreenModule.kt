package com.worldventures.wallet.ui.wizard.records.finish.impl

import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(PaymentSyncFinishScreenImpl::class), complete = false)
class PaymentSyncFinishScreenModule {

   @Provides
   fun providePaymentSyncFinishPresenter(navigator: Navigator,
                                         deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                         wizardInteractor: WizardInteractor,
                                         analyticsInteractor: WalletAnalyticsInteractor): PaymentSyncFinishPresenter =
         PaymentSyncFinishPresenterImpl(navigator, deviceConnectionDelegate, wizardInteractor, analyticsInteractor)

}
