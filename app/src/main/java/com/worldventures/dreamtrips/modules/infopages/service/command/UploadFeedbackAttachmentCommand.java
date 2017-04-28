package com.worldventures.dreamtrips.modules.infopages.service.command;

import android.content.Context;
import android.net.Uri;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.uploadery.UploadFeedbackImageHttpAction;
import com.worldventures.dreamtrips.api.uploadery.model.UploaderyImageResponse;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UploadFeedbackAttachmentCommand extends CommandWithError implements InjectableAction {

   @Inject Janet janet;
   @Inject @ForApplication Context context;
   @Inject StaticPageProvider staticPageProvider;

   private EntityStateHolder<FeedbackImageAttachment> entityStateHolder;

   public UploadFeedbackAttachmentCommand(FeedbackImageAttachment imageAttachment) {
      this.entityStateHolder = EntityStateHolder.create(imageAttachment,
            EntityStateHolder.State.PROGRESS);
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
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
      try {
         return janet.createPipe(UploadFeedbackImageHttpAction.class)
               .createObservableResult(new UploadFeedbackImageHttpAction(staticPageProvider.getUploaderyUrl(), file))
               .map(UploadFeedbackImageHttpAction::response);
      } catch (IOException ex) {
         return Observable.error(ex);
      }
   }

   private Observable<File> copyFileIfNeeded(String filePath) {
      return Observable.fromCallable(() -> {
         String path = UploadingFileManager.copyFileIfNeed(filePath, context);
         return new File(FileUtils.getPath(context, Uri.parse(path)));
      });
   }

   public EntityStateHolder<FeedbackImageAttachment> getEntityStateHolder() {
      return entityStateHolder;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.feedback_could_not_load_attachment;
   }
}
