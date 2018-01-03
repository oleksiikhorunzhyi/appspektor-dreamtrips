package com.worldventures.dreamtrips.modules.config.service

import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.core.test.AssertUtil.assertActionSuccessSkipStart
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.modules.config.model.Configuration
import com.worldventures.dreamtrips.modules.config.model.TravelBannerRequirement
import com.worldventures.dreamtrips.modules.config.model.UpdateRequirement
import com.worldventures.dreamtrips.modules.config.model.VideoRequirement
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class AppConfigurationInteractorSpec : BaseSpec({
   describe("App configuration command interactions") {

      it("Should send ConfigurationCommand if LoadConfigurationCommand succeed") {
         setup(Contract.of(LoadConfigurationCommand::class.java).result(stubConfig))

         val testSubscriber = TestSubscriber<ActionState<ConfigurationCommand>>()
         appConfigInteractor.configurationPipe.observe().subscribe(testSubscriber)
         appConfigInteractor.loadConfigPipe.send(LoadConfigurationCommand())

         assertActionSuccess(testSubscriber) {
            it.result == stubConfig
         }
      }

      it("Should not send ConfigurationCommand if LoadConfigurationCommand fails") {
         setup(Contract.of(LoadConfigurationCommand::class.java).exception(RuntimeException()))

         val testSubscriber = TestSubscriber<ActionState<ConfigurationCommand>>()
         appConfigInteractor.configurationPipe.observe().subscribe(testSubscriber)
         appConfigInteractor.loadConfigPipe.send(LoadConfigurationCommand())

         testSubscriber.unsubscribe()
         testSubscriber.assertNoErrors()
         testSubscriber.assertNoValues()
      }

      it("Should remove travel banner config from result") {
         setup()
         val testSubscriber = TestSubscriber<ActionState<ConfigurationCommand>>()
         appConfigInteractor.configurationPipe.observe().subscribe(testSubscriber)
         appConfigInteractor.configurationPipe.send(ConfigurationCommand(hideTravelConfig = true))

         assertActionSuccessSkipStart(testSubscriber) { it.result.travelBannerRequirement == null }
      }

      it("Should return default config if config is not passed") {
         setup()
         val testSubscriber = TestSubscriber<ActionState<ConfigurationCommand>>()
         appConfigInteractor.configurationPipe.observe().subscribe(testSubscriber)
         appConfigInteractor.configurationPipe.send(ConfigurationCommand())

         assertActionSuccessSkipStart(testSubscriber) { it.result != null }
      }
   }
}) {
   companion object {

      val stubConfig = Configuration(UpdateRequirement(), VideoRequirement(), TravelBannerRequirement())

      lateinit var appConfigInteractor: AppConfigurationInteractor

      fun setup(vararg contracts: Contract) {
         val commandActionService = MockCommandActionService.Builder()
               .actionService(CommandActionService())
               .apply {
                  contracts.forEach { addContract(it) }
               }.build()

         val janet = Janet.Builder()
               .addService(commandActionService)
               .build()

         appConfigInteractor = AppConfigurationInteractor(janet)
      }
   }
}
