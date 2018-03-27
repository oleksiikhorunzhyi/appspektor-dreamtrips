package com.worldventures.core.modules.legal

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.legal.command.AcceptTermsCommand
import com.worldventures.core.modules.legal.command.GetDocumentByTypeCommand
import rx.schedulers.Schedulers

class LegalInteractor(pipeCreator: SessionActionPipeCreator) {
   val acceptTermsPipe = pipeCreator.createPipe(AcceptTermsCommand::class.java, Schedulers.io())
   val getDocumentByTypePipe = pipeCreator.createPipe(GetDocumentByTypeCommand::class.java, Schedulers.io())
}
