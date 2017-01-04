package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TranslatePhotoCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class TranslationFeedInteractor {

   private final ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe;
   private final ActionPipe<TranslateUidItemCommand.TranslatePostCommand> translatePostPipe;
   private final ActionPipe<TranslatePhotoCommand> translatePhotoPipe;

   @Inject
   public TranslationFeedInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      translateCommentPipe = sessionActionPipeCreator.createPipe(TranslateUidItemCommand.TranslateCommentCommand.class,
            Schedulers.io());
      translatePostPipe = sessionActionPipeCreator.createPipe(TranslateUidItemCommand.TranslatePostCommand.class,
            Schedulers.io());
      translatePhotoPipe = sessionActionPipeCreator.createPipe(TranslatePhotoCommand.class, Schedulers.io());
   }

   public ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe() {
      return translateCommentPipe;
   }

   public ActionPipe<TranslateUidItemCommand.TranslatePostCommand> translatePostPipe() {
      return translatePostPipe;
   }

   public ActionPipe<TranslatePhotoCommand> translatePhotoPipe() {
      return translatePhotoPipe;
   }

}
