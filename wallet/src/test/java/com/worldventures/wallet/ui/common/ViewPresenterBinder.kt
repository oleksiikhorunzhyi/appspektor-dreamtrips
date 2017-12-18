package com.worldventures.wallet.ui.common

import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

class ViewPresenterBinder<V : WalletScreen, out P : WalletPresenter<V>>(val view: V, val presenter: P) {

   fun bind() {
      presenter.attachView(view)
   }

   fun unbind() {
      presenter.detachView(true)
   }

}
