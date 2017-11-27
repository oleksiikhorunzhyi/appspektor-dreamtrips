package com.worldventures.wallet.ui.settings.help.feedback.base.impl

import android.os.Bundle

import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter

abstract class BaseFeedbackScreenImpl<S : BaseFeedbackScreen, P : BaseSendFeedbackPresenter<*>> : WalletBaseController<S, P>, BaseFeedbackScreen {

   constructor() : super()

   constructor(args: Bundle) : super(args)

   override fun pickPhoto(count: Int) {
      val mediaPickerDialog = MediaPickerDialog(context)
      mediaPickerDialog.setOnDoneListener {
         if (!it.isEmpty) {
            presenter.handleAttachedImages(it.chosenImages)
         }
      }
      mediaPickerDialog.show(count)
   }
}
