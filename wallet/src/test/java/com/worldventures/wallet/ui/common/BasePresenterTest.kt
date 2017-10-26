package com.worldventures.wallet.ui.common

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.wallet.ui.common.base.WalletPresenter
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.common.navigation.Navigator
import org.junit.After
import org.junit.Before

abstract class BasePresenterTest<V : WalletScreen, P : WalletPresenter<V>> : BaseTest() {

   lateinit var navigator: Navigator
   lateinit var viewPresenterBinder: ViewPresenterBinder<V, P>

   @Before
   fun beforeTest() {
      navigator = mock()
      setup()
      viewPresenterBinder = createViewPresenterBinder()
      viewPresenterBinder.bind()
   }

   @After
   fun afterTest() {
      viewPresenterBinder.unbind()
   }

   abstract fun createViewPresenterBinder(): ViewPresenterBinder<V, P>

   open fun setup() {
   }
}
