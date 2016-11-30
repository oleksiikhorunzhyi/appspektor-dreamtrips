package com.worldventures.dreamtrips.social.common.connection_overlay

import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.core.SocialConnectionOverlay
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayView
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayViewFactory
import rx.Observable
import rx.lang.kotlin.PublishSubject

class SocialConnectionOverlaySpec : BaseSpec({
   describe("Test social connection overlay") {
      val socialConnectionOverlayFactory: SocialConnectionOverlayViewFactory = mock()
      val socialConnectionOverlayView: SocialConnectionOverlayView = mock()

      whenever(socialConnectionOverlayFactory.createOverlayView()).thenReturn(socialConnectionOverlayView)
      whenever(socialConnectionOverlayView.closeClickObservable).thenReturn(Observable.empty())

      val connectionOverlay = SocialConnectionOverlay(socialConnectionOverlayFactory)
      val connectionSubject = PublishSubject<ConnectionState>()

      connectionOverlay.startProcessingState(connectionSubject, Observable.never<Void>())

      connectionSubject.onNext(ConnectionState.CONNECTED)

      it("Should hide overlay") {
         verify(socialConnectionOverlayView, atLeastOnce()).hide()
      }

      connectionSubject.onNext(ConnectionState.DISCONNECTED)

      it("Should show overlay") {
         verify(socialConnectionOverlayView, atLeastOnce()).show()
      }
   }
})