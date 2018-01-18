package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.SpecBody

abstract class AbstractPresenterSpec(testBody: TestBody<*, *>) : PresenterBaseSpec(testBody.createTestBody()) {

   interface TestBody<V : Presenter.View, P : Presenter<V>> {
      fun init()

      fun createTestSuits(): List<SpecBody.() -> Unit>

      fun createTestBody(): Spec.() -> Unit

      fun mockPresenter(): P

      fun mockView(): V

      fun prepareInjection(presenter: P): Injector
   }
}