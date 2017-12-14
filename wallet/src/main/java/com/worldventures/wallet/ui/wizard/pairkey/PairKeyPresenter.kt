package com.worldventures.wallet.ui.wizard.pairkey

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface PairKeyPresenter : WalletPresenter<PairKeyScreen> {

   fun tryToPairAndConnectSmartCard()

   fun goBack()
}
