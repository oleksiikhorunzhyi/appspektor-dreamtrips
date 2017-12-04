package com.worldventures.wallet.ui.settings.help.feedback.regular

import com.worldventures.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen

import rx.Observable

interface SendFeedbackScreen : BaseFeedbackScreen {

   val feedbackMessage: String

   val textFeedbackMessage: Observable<CharSequence>

   val feedbackType: FeedbackType

   fun applyFeedbackType(feedbackType: FeedbackType)
}
