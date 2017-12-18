package com.worldventures.core.modules.infopages.service

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import io.techery.janet.ActionPipe
import rx.schedulers.Schedulers

class DocumentsInteractor(sessionActionPipeCreator: SessionActionPipeCreator) {

   val documentsActionPipe: ActionPipe<GetDocumentsCommand> = sessionActionPipeCreator
         .createPipe(GetDocumentsCommand::class.java, Schedulers.io())
}
