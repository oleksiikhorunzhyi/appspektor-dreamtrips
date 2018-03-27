package com.worldventures.core.modules.legal.command

import com.worldventures.dreamtrips.api.terms_and_conditions.GetDocumentByTypeHttpAction
import com.worldventures.dreamtrips.api.terms_and_conditions.model.DocumentBodyWithUrl
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class GetDocumentByTypeCommand(private val type: String) : Command<DocumentBodyWithUrl>(), InjectableAction {

   @Inject lateinit var janet: Janet

   override fun run(callback: CommandCallback<DocumentBodyWithUrl>) {
      janet.createPipe(GetDocumentByTypeHttpAction::class.java)
            .createObservableResult(GetDocumentByTypeHttpAction(type))
            .subscribe({ callback.onSuccess(it.response()) }) { callback.onFail(it) }
   }
}
