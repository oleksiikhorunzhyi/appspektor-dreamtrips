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

   private final ActionPipe<TranslateUidItemCommand.TranslateFeedEntityCommand> translateFeedEntityPipe;
   private final ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe;
   private final ActionPipe<TranslatePhotoCommand> translatePhotoPipe;

   @Inject
   public TranslationFeedInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      translateFeedEntityPipe = sessionActionPipeCreator.createPipe(TranslateUidItemCommand.TranslateFeedEntityCommand.class,
            Schedulers.io());
      translateCommentPipe = sessionActionPipeCreator.createPipe(TranslateUidItemCommand.TranslateCommentCommand.class,
            Schedulers.io());
      translatePhotoPipe = sessionActionPipeCreator.createPipe(TranslatePhotoCommand.class, Schedulers.io());
   }

   public ActionPipe<TranslateUidItemCommand.TranslateFeedEntityCommand> translateFeedEntityPipe() {
      return translateFeedEntityPipe;
   }

   public ActionPipe<TranslateUidItemCommand.TranslateCommentCommand> translateCommentPipe() {
      return translateCommentPipe;
   }

   public ActionPipe<TranslatePhotoCommand> translatePhotoPipe() {
      return translatePhotoPipe;
   }

}
