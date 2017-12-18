package com.worldventures.wallet.ui.settings.help.feeback

import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor
import com.worldventures.wallet.ui.common.BasePresenterTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.ui.common.MockDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.ViewPresenterBinder
import com.worldventures.wallet.ui.settings.help.feeback.payment.MockFeedbackAttachmentsDelegate
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackScreen
import com.worldventures.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackPresenterImpl
import org.junit.Test
import rx.lang.kotlin.PublishSubject
import rx.subjects.PublishSubject

class PaymentFeedbackPresenterTest : BasePresenterTest<PaymentFeedbackScreen, PaymentFeedbackPresenter>() {

   private lateinit var presenter: PaymentFeedbackPresenter
   private lateinit var view: PaymentFeedbackScreen
   private lateinit var settingsInteractor: WalletSettingsInteractor
   private lateinit var feedbackAttachmentsDelegate: MockFeedbackAttachmentsDelegate

   private val merchantNameObservable: PublishSubject<CharSequence> = PublishSubject()

   private val interactorBuilder = InteractorBuilder.configJanet {
   }

   override fun createViewPresenterBinder(): ViewPresenterBinder<PaymentFeedbackScreen, PaymentFeedbackPresenter>
         = ViewPresenterBinder(view, presenter)

   override fun setup() {
      view = mockScreen(PaymentFeedbackScreen::class.java)
      whenever(view.observeMerchantName()).thenReturn(merchantNameObservable)

      feedbackAttachmentsDelegate = MockFeedbackAttachmentsDelegate(2)

      navigator = mock()
      settingsInteractor = interactorBuilder.createInteractor(WalletSettingsInteractor::class)
      presenter = PaymentFeedbackPresenterImpl(navigator, MockDeviceConnectionDelegate(), feedbackAttachmentsDelegate, settingsInteractor)
   }

   @Test
   fun testMessageInput() {
      verify(view, times(1)).changeActionSendMenuItemEnabled(false)
      reset(view)

      merchantNameObservable.onNext("    ")
      verify(view, times(1)).changeActionSendMenuItemEnabled(false)
      reset(view)

      merchantNameObservable.onNext("    Test")
      verify(view, times(1)).changeActionSendMenuItemEnabled(true)
   }

   @Test
   fun testAddingSuccessAttachments() {
      feedbackAttachmentsDelegate.addSuccessAttachment()
      verify(view, times(0)).changeActionSendMenuItemEnabled(true)
      verify(view, atLeastOnce()).changeActionSendMenuItemEnabled(false)
      reset(view)

      merchantNameObservable.onNext("Test")
      verify(view, times(1)).changeActionSendMenuItemEnabled(true)
   }

   @Test
   fun testAddingProgressAttachments() {
      feedbackAttachmentsDelegate.addProgressAttachments()
      verify(view, times(0)).changeActionSendMenuItemEnabled(true)
      verify(view, atLeastOnce()).changeActionSendMenuItemEnabled(false)
      reset(view)

      merchantNameObservable.onNext("Test")
      verify(view, times(1)).changeActionSendMenuItemEnabled(false)
   }

   @Test
   fun testAddingFailedAttachments() {
      feedbackAttachmentsDelegate.addFailedAttachments()
      verify(view, times(0)).changeActionSendMenuItemEnabled(true)
      verify(view, atLeastOnce()).changeActionSendMenuItemEnabled(false)
      reset(view)

      merchantNameObservable.onNext("Test")
      verify(view, times(1)).changeActionSendMenuItemEnabled(false)
   }
}