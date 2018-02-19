package com.worldventures.wallet.ui.settings.general.reset.delegate

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.ui.common.navigation.Navigator
import dagger.Module
import dagger.Provides

@Module(complete = false, library = true)
class FactoryResetDelegateModule {

   @Provides
   fun factoryResetDelegate(
         smartCardInteractor: SmartCardInteractor,
         factoryResetInteractor: FactoryResetInteractor,
         navigator: Navigator) = FactoryResetDelegateFactory(smartCardInteractor, factoryResetInteractor, navigator)
}
