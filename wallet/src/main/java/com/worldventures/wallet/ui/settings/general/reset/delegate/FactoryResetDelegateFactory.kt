package com.worldventures.wallet.ui.settings.general.reset.delegate

import com.worldventures.wallet.service.FactoryResetInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType.DASHBOARD
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType.NEW_CARD
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType.NEW_CARD_ENTER_PIN
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType.SETTINGS
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetType.SETTINGS_ENTER_PIN

class FactoryResetDelegateFactory(
      private val smartCardInteractor: SmartCardInteractor,
      private val factoryResetInteractor: FactoryResetInteractor,
      private val navigator: Navigator) {

   fun createFactoryResetDelegate(factoryResetType: FactoryResetType): FactoryResetDelegate {
      return when (factoryResetType) {
         DASHBOARD, SETTINGS -> GeneralResetDelegateCheckPin(smartCardInteractor, factoryResetInteractor, navigator)
         SETTINGS_ENTER_PIN -> GeneralFactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator)
         NEW_CARD -> NewCardFactoryResetDelegateCheckPin(smartCardInteractor, factoryResetInteractor, navigator)
         NEW_CARD_ENTER_PIN -> NewCardFactoryResetDelegate(smartCardInteractor, factoryResetInteractor, navigator)
      }
   }
}
