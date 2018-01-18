package com.worldventures.core.modules.legal;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.legal.command.AcceptTermsCommand;
import com.worldventures.core.modules.legal.command.GetDocumentByTypeCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            AcceptTermsCommand.class,
            GetDocumentByTypeCommand.class,
      },
      library = true,
      complete = false)
public class LegalModule {

   @Provides
   @Singleton
   LegalInteractor provideFeedbackInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new LegalInteractor(sessionActionPipeCreator);
   }

}
