package com.worldventures.wallet.ui.wizard.input.scanner.impl

import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.input.helper.InputAnalyticsDelegate
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegateImpl
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter
import dagger.Module
import dagger.Provides

@Module(injects = arrayOf(WizardScanBarcodeScreenImpl::class), complete = false)
class WizardScanBarcodeScreenModule {

   @Provides
   fun provideWizardScanBarcodePresenter(navigator: Navigator,
                                         deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                         wizardInteractor: WizardInteractor,
                                         analyticsInteractor: WalletAnalyticsInteractor,
                                         permissionDispatcher: PermissionDispatcher,
                                         smartCardInteractor: SmartCardInteractor): WizardScanBarcodePresenter =
         WizardScanBarcodePresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher,
               InputBarcodeDelegateImpl(navigator, wizardInteractor,
                     InputAnalyticsDelegate.createForScannerScreen(analyticsInteractor), smartCardInteractor))

}
