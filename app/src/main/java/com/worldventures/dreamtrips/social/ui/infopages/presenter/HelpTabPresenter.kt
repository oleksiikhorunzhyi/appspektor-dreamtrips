package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.dreamtrips.modules.common.presenter.Presenter

class HelpTabPresenter : Presenter<Presenter.View>() {

   override fun takeView(view: View?) {
      super.takeView(view)
      subscribeToErrorUpdates()
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private fun subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { reportNoConnection() }
   }
}
