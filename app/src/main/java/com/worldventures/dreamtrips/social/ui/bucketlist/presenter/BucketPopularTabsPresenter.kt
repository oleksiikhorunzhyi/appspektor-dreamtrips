package com.worldventures.dreamtrips.social.ui.bucketlist.presenter

import android.os.Bundle
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem

class BucketPopularTabsPresenter : Presenter<Presenter.View>() {

   fun getBundleForPosition(position: Int): Bundle {
      val args = Bundle()
      val type = BucketItem.BucketType.values()[position]
      args.putSerializable(BucketPopularTabsPresenter.EXTRA_TYPE, type)
      return args
   }

   override fun takeView(view: Presenter.View) {
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

   companion object {

      val EXTRA_TYPE = "EXTRA_TYPE"
   }
}
