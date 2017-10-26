package com.worldventures.wallet.ui.common

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.janet.MockAnalyticsService
import com.worldventures.wallet.service.TestSchedulerProvider
import com.worldventures.wallet.service.WalletSchedulerProvider
import io.techery.janet.ActionService
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.command.test.MockCommandActionService
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.client.SmartCardClient
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType

class InteractorBuilder private constructor(val janet: Janet) {

   fun <Interactor : Any> createInteractor(clazz: KClass<Interactor>): Interactor {
      val pipeCreator = SessionActionPipeCreator(janet)
      val constructor = clazz.constructors.find {
         it.parameters.size == 1
               && it.parameters[0].type.javaType == SessionActionPipeCreator::class.java
      }
      val constructorWithScheduler = clazz.constructors.find {
         it.parameters.size == 2
               && it.parameters[0].type.javaType == SessionActionPipeCreator::class.java
               && it.parameters[1].type.javaType == WalletSchedulerProvider::class.java
      }
      if (constructor != null) {
         return constructor.call(pipeCreator)
      } else if (constructorWithScheduler != null) {
         return constructorWithScheduler.call(pipeCreator, TestSchedulerProvider())
      } else {
         throw UnsupportedOperationException("Interactor should have constructor with SessionActionPipeCreator argument")
      }
   }

   companion object {
      fun configJanet(init: JanetBuilder.() -> Unit): InteractorBuilder {
         val builder = JanetBuilder()
         builder.init()
         return InteractorBuilder(builder.build())
      }
   }
}

class JanetBuilder {
   val builder = Janet.Builder()

   fun addService(actionService: ActionService) {
      builder.addService(actionService)
   }

   fun addMockAnalyticsService() {
      addService(MockAnalyticsService())
   }

   fun addMockSmartCardActionService() {
      addMockSmartCardActionService(MockSmartCardClient())
   }

   fun addMockSmartCardActionService(smartCardClient: SmartCardClient) {
      addService(SmartCardActionService.createDefault(smartCardClient))
   }

   fun addMockCommandActionService(init: MockCommandActionService.Builder.() -> Unit) {
      val builder = MockCommandActionService.Builder()
      builder.actionService(CommandActionService())
      builder.init()
      addService(builder.build())
   }

   fun build(): Janet = builder.build()
}

fun Janet.connectToSmartCard() {
   this.createPipe(ConnectAction::class.java).send(ConnectAction(ImmutableConnectionParams.of(104)))
}