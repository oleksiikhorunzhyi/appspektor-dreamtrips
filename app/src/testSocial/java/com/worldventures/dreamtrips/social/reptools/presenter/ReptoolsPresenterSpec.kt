package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.session.Feature
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.membership.view.fragment.InviteFragment
import com.worldventures.dreamtrips.social.ui.reptools.presenter.RepToolsPresenter
import com.worldventures.dreamtrips.social.util.event_delegate.SearchFocusChangedDelegate
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals

class ReptoolsPresenterSpec : PresenterBaseSpec(ReptoolsTestSuite()) {

   class ReptoolsTestSuite : TestSuite<ReptoolsComponents>(ReptoolsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Reptools Presenter") {

               beforeEachTest {
                  init()
                  presenter.onInjected()
                  linkPresenterAndView()
               }

               it("should set screens on take view") {
                  verify(view).setScreens(any())
               }

               it("should provide 5 screens with Invite screen") {
                  whenever(featureManager.available(Feature.REP_TOOLS)).thenReturn(true)
                  whenever(featureManager.available(Feature.INVITATIONS)).thenReturn(true)

                  val screens = presenter.provideScreens()

                  assertEquals(presenter.provideScreens().size, 5)
                  assert(screens.filter { it.fragmentClazz == InviteFragment::class.java }.count() == 1)
               }

               it("should provide 4 screens without Invite screen") {
                  whenever(featureManager.available(Feature.REP_TOOLS)).thenReturn(false)
                  whenever(featureManager.available(Feature.INVITATIONS)).thenReturn(false)

                  val screens = presenter.provideScreens()

                  assertEquals(presenter.provideScreens().size, 4)
                  assert(screens.filter { it.fragmentClazz == InviteFragment::class.java }.count() == 0)
               }
            }
         }
      }
   }

   class ReptoolsComponents : TestComponents<RepToolsPresenter, RepToolsPresenter.View>() {

      fun init() {
         presenter = RepToolsPresenter()
         view = mock()

         prepareInjector().apply {
            registerProvider(SearchFocusChangedDelegate::class.java, { SearchFocusChangedDelegate() })
            registerProvider(OfflineErrorInteractor::class.java, { makeOfflineErrorInteractor() })
            inject(presenter)
         }
      }

      private fun makeOfflineErrorInteractor(): OfflineErrorInteractor {
         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(OfflineErrorCommand::class.java).result(null))
         }.build()
         val janet = Janet.Builder().addService(service).build()
         return OfflineErrorInteractor(SessionActionPipeCreator(janet))
      }
   }
}
