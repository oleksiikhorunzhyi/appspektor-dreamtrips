package com.worldventures.wallet.ui.common.helper2.error

import com.worldventures.wallet.ui.common.base.screen.LifecycleHolder
import com.worldventures.wallet.ui.common.helper2.progress.DialogProgressView
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.ErrorView
import io.techery.janet.operationsubscriber.view.SuccessView
import rx.Subscription

open class DetachableOperationView<T>(
      progressView: DialogProgressView<T>?,
      successView: SuccessView<T>?,
      errorView: ErrorView<T>?,
      private val lifecycleHolder: LifecycleHolder)
   : ComposableOperationView<T>(progressView, successView, errorView) {

   constructor(progressView: DialogProgressView<T>, errorView: ErrorView<T>, view: LifecycleHolder):
         this(progressView, null, errorView, view)

   private var subscription: Subscription? = null

   override fun hideProgress() {
      super.hideProgress()
      subscription?.let { if (!it.isUnsubscribed) it.unsubscribe() }
   }

   override fun showProgress(action: T) {
      super.showProgress(action)
      subscription = lifecycleHolder.detachObservable().take(1).subscribe { hideProgress() }
   }
}
