package com.worldventures.core.modules.legal.command

import com.worldventures.dreamtrips.api.terms_and_conditions.AcceptTermsAndConditionsHttpAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class AcceptTermsCommand(private val type: String, private val version: String) : Command<Void>(), InjectableAction {

   @Inject lateinit var janet: Janet

   override fun run(callback: Command.CommandCallback<Void>) {
      janet.createPipe(AcceptTermsAndConditionsHttpAction::class.java)
            .createObservableResult(AcceptTermsAndConditionsHttpAction(type, version))
            .subscribe({ callback.onSuccess(null) }) { callback.onFail(it) }
   }
}
