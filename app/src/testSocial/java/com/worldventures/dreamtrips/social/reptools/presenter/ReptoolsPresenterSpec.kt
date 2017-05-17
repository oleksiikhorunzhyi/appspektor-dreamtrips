package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.*
import com.techery.spares.utils.delegate.SearchFocusChangedDelegate
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.navigation.Route
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertEquals

class ReptoolsPresenterSpec: PresenterBaseSpec({

   describe("Reptools Presenter") {

      context("take view") {
         init()
         presenter.takeView(view)
         it ("should set screens on take view") {
            verify(view, VerificationModeFactory.times(1)).setScreens(any())
         }
      }

      it ("should provide 4 screens with Invite screen") {
         init()
         doReturn(true).whenever(presenter).showInvite()
         val screens = presenter.provideScreens()
         assertEquals(presenter.provideScreens().size, 4)
         assert(screens.filter{ it.route == Route.INVITE }.count() == 1)
      }

      it ("should provide 3 screens without Invite screen") {
         init()
         doReturn(false).whenever(presenter).showInvite()
         val screens = presenter.provideScreens()
         assertEquals(presenter.provideScreens().size, 3)
         assert(screens.filter{ it.route == Route.INVITE }.count() == 0)
      }
   }

}) {
   companion object BaseCompanion {
      lateinit var presenter: RepToolsPresenter
      lateinit var view: RepToolsPresenter.View
      lateinit var mockDb: SnappyRepository

      fun init() {
         presenter = spy(RepToolsPresenter())
         view = spy()

         prepareInjector().apply {
            registerProvider(SearchFocusChangedDelegate::class.java, { SearchFocusChangedDelegate() })
            registerProvider(OfflineErrorInteractor::class.java, { makeOfflineErrorInteractor() })
            inject(presenter)
         }
         presenter.onInjected()
      }

      fun makeOfflineErrorInteractor(): OfflineErrorInteractor {
         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(OfflineErrorCommand::class.java).result(null))
         }.build()
         val janet = Janet.Builder().addService(service).build()
         return OfflineErrorInteractor(SessionActionPipeCreator(janet))
      }
   }
}

