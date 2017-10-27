package com.worldventures.wallet.ui.wizard.input.manual

import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.wizard.input.helper.InputDelegateView

import rx.Observable

interface WizardManualInputScreen : WalletScreen, InputDelegateView {

   fun getScIdLength(): Int

   fun buttonEnable(isEnable: Boolean)

   fun scidInput(): Observable<CharSequence>
}