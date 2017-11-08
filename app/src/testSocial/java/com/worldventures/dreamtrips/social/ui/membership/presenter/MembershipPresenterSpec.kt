package com.worldventures.dreamtrips.social.ui.membership.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.session.Feature
import com.worldventures.dreamtrips.core.navigation.Route
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import io.techery.janet.Janet
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class MembershipPresenterSpec: PresenterBaseSpec({

   describe("Membership Presenter") {

      init()

      describe("take view") {
         presenter.takeView(view)

         it("should set screens on take view") {
            verify(view).setScreens(any())
         }

         it ("should subscribe to offline error pipe") {
            verify(offlineErrorInteractor).offlineErrorCommandPipe()
         }
      }

      describe("screens list") {

         it("should contain all screens: presentation videos, enroll member, enroll merchant, invite and share, podcasts") {
            whenever(featureManager.available(Feature.REP_SUGGEST_MERCHANT)).thenReturn(true)
            whenever(featureManager.available(Feature.REP_TOOLS)).thenReturn(false)
            whenever(featureManager.available(Feature.MEMBERSHIP)).thenReturn(true)

            val screens = presenter.provideScreens()
            assertTrue { screens.contains(Route.PRESENTATION_VIDEOS) }
            assertTrue { screens.contains(Route.ENROLL_MEMBER) }
            assertTrue { screens.contains(Route.ENROLL_MERCHANT) }
            assertTrue { screens.contains(Route.INVITE) }
            assertTrue { screens.contains(Route.PODCASTS) }
         }

         it("should contain only presentation videos, enroll member screens") {
            whenever(featureManager.available(Feature.REP_SUGGEST_MERCHANT)).thenReturn(false)
            whenever(featureManager.available(Feature.REP_TOOLS)).thenReturn(true)
            whenever(featureManager.available(Feature.MEMBERSHIP)).thenReturn(false)

            val screens = presenter.provideScreens()
            assertTrue { screens.contains(Route.PRESENTATION_VIDEOS) }
            assertTrue { screens.contains(Route.ENROLL_MEMBER) }
            assertTrue { !screens.contains(Route.ENROLL_MERCHANT) }
            assertTrue { !screens.contains(Route.INVITE) }
            assertTrue { !screens.contains(Route.PODCASTS) }
         }
      }
   }

}) {
   companion object {
      lateinit var presenter: MembershipPresenter
      lateinit var view: MembershipPresenter.View
      lateinit var offlineErrorInteractor: OfflineErrorInteractor

      fun init() {
         presenter = MembershipPresenter()
         view = spy()

         offlineErrorInteractor = mock()
         val pipeCreator = SessionActionPipeCreator(Janet.Builder().build())
         whenever(offlineErrorInteractor.offlineErrorCommandPipe()).thenReturn(pipeCreator.createPipe(OfflineErrorCommand::class.java))

         prepareInjector().apply {
            registerProvider(OfflineErrorInteractor::class.java, { offlineErrorInteractor })
            inject(presenter)
         }
      }

      fun List<FragmentItem>.contains(route: Route) = this.any{ it.route == route }
   }
}