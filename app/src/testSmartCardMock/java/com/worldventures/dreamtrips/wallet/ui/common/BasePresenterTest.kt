package com.worldventures.dreamtrips.wallet.ui.common

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService
import com.worldventures.dreamtrips.wallet.service.impl.MockWalletNetworkService
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator
import org.junit.After
import org.junit.Before

abstract class BasePresenterTest<V : WalletScreen, P : WalletPresenter<V>> : BaseTest() {

   val walletNetworkService: WalletNetworkService = MockWalletNetworkService()
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
