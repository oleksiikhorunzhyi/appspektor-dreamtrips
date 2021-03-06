package com.worldventures.dreamtrips.modules.common.command;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.storage.dao.PhotoDAO;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.picker.service.PickImageDelegate;
import com.worldventures.core.ui.util.DrawableUtil;
import com.worldventures.core.utils.FileUtils;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class CleanTempDirectoryCommand extends Command implements InjectableAction {

   @ForApplication @Inject Context context;
   @Inject DrawableUtil drawableUtil;
   @Inject PhotoDAO photoDAO;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      Observable.combineLatest(
            compoundOperationsInteractor.compoundOperationsPipe()
                  .createObservableResult(new QueryCompoundOperationsCommand()),
            photoDAO.getErrorAttachments().take(1),
            (queryCompoundOperationsCommand, dataPhotoAttachments) -> {
               List<String> exceptFilePaths = new ArrayList<>();
               exceptFilePaths.addAll(Queryable.from(dataPhotoAttachments).map(DataPhotoAttachment::getUrl).toList());
               exceptFilePaths.addAll(obtainMediaFilesPaths(queryCompoundOperationsCommand.getResult()));
               return exceptFilePaths;
            })
            .subscribe(exceptFilePaths -> {
               File directory = new File(FileUtils.getDirectory(PickImageDelegate.FOLDERNAME));
               if (!directory.exists()) {
                  callback.onSuccess(null);
                  return;
               }
               try {
                  FileUtils.cleanDirectory(context, directory, exceptFilePaths);
                  callback.onSuccess(null);
               } catch (IOException e) {
                  callback.onFail(e);
               }

               drawableUtil.removeCacheImages(exceptFilePaths);
            }, callback::onFail);
   }

   private List<String> obtainMediaFilesPaths(List<PostCompoundOperationModel> compoundOperationModels) {
      List<String> mediaFilePaths = new ArrayList<>();
      for (PostCompoundOperationModel compoundOperationModel : compoundOperationModels) {
         if (compoundOperationModel.type() == PostBody.Type.PHOTO) {
            mediaFilePaths.addAll(Queryable.from(((PostWithPhotoAttachmentBody) compoundOperationModel.body()).attachments())
                  .map(attachment -> attachment.selectedPhoto().path()).toList());
         }

         if (compoundOperationModel.type() == PostBody.Type.VIDEO) {
            mediaFilePaths.add(((PostWithVideoAttachmentBody) compoundOperationModel.body()).videoPath());
         }
      }
      return mediaFilePaths;
   }

}
