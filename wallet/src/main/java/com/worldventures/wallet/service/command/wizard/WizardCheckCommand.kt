package com.worldventures.wallet.service.command.wizard

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.service.WalletBluetoothService
import com.worldventures.wallet.service.WalletNetworkService
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
// TODO: 11/11/17 remove command
class WizardCheckCommand : Command<WizardCheckCommand.Checks>(), InjectableAction {

   @Inject lateinit var bluetoothService: WalletBluetoothService
   @Inject lateinit var networkService: WalletNetworkService

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Checks>) {
      callback.onSuccess(Checks(
            bleIsSupported = bluetoothService.isSupported,
            bluetoothIsEnabled = bluetoothService.isEnable,
            internetIsAvailable = networkService.isAvailable))
   }

   data class Checks(
      val bleIsSupported: Boolean,
      val bluetoothIsEnabled: Boolean,
      val internetIsAvailable: Boolean
   )
}
