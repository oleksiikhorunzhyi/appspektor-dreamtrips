package com.worldventures.core.modules.infopages.service.command;

import android.net.Uri;

import com.worldventures.core.R;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.core.service.UploadingFileManager;
import com.worldventures.core.service.UriPathProvider;
import com.worldventures.dreamtrips.api.uploadery.UploadFeedbackImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse;
import com.worldventures.janet.injection.InjectableAction;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UploadFeedbackAttachmentCommand extends CommandWithError implements InjectableAction {

   @Inject Janet janet;
   @Inject UploadingFileManager uploadingFileManager;
   @Inject StaticPageProvider staticPageProvider;
   @Inject UriPathProvider uriPathProvider;

   private EntityStateHolder<FeedbackImageAttachment> entityStateHolder;
   private ActionPipe<UploadFeedbackImageHttpAction> actionActionPipe;

   public UploadFeedbackAttachmentCommand(FeedbackImageAttachment imageAttachment) {
      this.entityStateHolder = EntityStateHolder.create(imageAttachment,
            EntityStateHolder.State.PROGRESS);
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      actionActionPipe = janet.createPipe(UploadFeedbackImageHttpAction.class);
      String originalPath = entityStateHolder.entity().getOriginalFilePath();
      entityStateHolder.entity().setUrl(originalPath);
      callback.onProgress(0);
      copyFileIfNeeded(originalPath)
            .flatMap(this::uploadFile)
            .subscribe(uploaderyImageResponse -> {
               entityStateHolder.setState(EntityStateHolder.State.DONE);
               entityStateHolder.entity().setUrl(uploaderyImageResponse.uploaderyPhoto().location());
               callback.onSuccess(entityStateHolder);
            }, throwable -> {
               entityStateHolder.setState(EntityStateHolder.State.FAIL);
               callback.onFail(throwable);
            });
   }

   private Observable<UploaderyImageResponse> uploadFile(File file) {
      return actionActionPipe
            .createObservableResult(new UploadFeedbackImageHttpAction(staticPageProvider.getUploaderyUrl(), file))
            .map(UploadFeedbackImageHttpAction::response);
   }

   private Observable<File> copyFileIfNeeded(String filePath) {
      return Observable.fromCallable(() -> {
         String path = uploadingFileManager.copyFileIfNeed(filePath);
         return new File(uriPathProvider.getPath(Uri.parse(path)));
      });
   }

   public EntityStateHolder<FeedbackImageAttachment> getEntityStateHolder() {
      return entityStateHolder;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.feedback_could_not_load_attachment;
   }

   @Override
   protected void cancel() {
      actionActionPipe.cancelLatest();
   }
}
