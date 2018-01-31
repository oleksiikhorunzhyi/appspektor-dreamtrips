package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.infopages.service.command.AttachmentsRemovedCommand
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import icepick.State
import java.util.ArrayList
import javax.inject.Inject

class FeedbackImageAttachmentsPresenter(initialAttachments: List<FeedbackImageAttachment>, private val initialPosition: Int)
   : Presenter<FeedbackImageAttachmentsPresenter.View>() {

   @State @JvmField var attachments = ArrayList(initialAttachments)
   @State @JvmField var locallyDeletedAttachments = ArrayList<FeedbackImageAttachment>()
   @Inject lateinit var backStackDelegate: BackStackDelegate
   @Inject lateinit var feedbackInteractor: FeedbackInteractor
   private val backPressedListener = BackStackDelegate.BackPressedListener {
      false.apply {
         if (locallyDeletedAttachments.isNotEmpty())
            feedbackInteractor.attachmentsRemovedPipe().send(AttachmentsRemovedCommand(locallyDeletedAttachments))
      }
   }

   override fun takeView(view: View) {
      super.takeView(view)
      view.addItems(attachments)
      view.setPosition(initialPosition)
      backStackDelegate.addListener(backPressedListener)
   }

   override fun dropView() {
      backStackDelegate.removeListener(backPressedListener)
      super.dropView()
   }

   fun onRemoveItem(position: Int) {
      locallyDeletedAttachments.add(attachments.removeAt(position))
      view.removeItem(position)
   }

   interface View : Presenter.View {

      fun addItems(imageAttachments: List<FeedbackImageAttachment>)

      fun setPosition(position: Int)

      fun removeItem(position: Int)
   }
}
