package com.worldventures.core.modules.legal.command

import com.worldventures.dreamtrips.api.terms_and_conditions.AcceptTermsAndConditionsHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class AcceptTermsCommand(private val type: String, private val version: String) : Command<Void>(), InjectableAction {
   private var smartCardId: String? = null

   constructor(type: String, version: String, smartCardId: String) : this(type, version) {
      this.smartCardId = smartCardId
   }

   @Inject lateinit var janet: Janet

   override fun run(callback: Command.CommandCallback<Void>) {
      janet.createPipe(AcceptTermsAndConditionsHttpAction::class.java)
            .createObservableResult(if (smartCardId == null)
               AcceptTermsAndConditionsHttpAction(type, version)
            else AcceptTermsAndConditionsHttpAction(type, version, smartCardId))
            .subscribe({ callback.onSuccess(null) }) { callback.onFail(it) }
   }
}
