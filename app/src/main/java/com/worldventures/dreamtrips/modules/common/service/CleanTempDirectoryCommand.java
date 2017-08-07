package com.worldventures.dreamtrips.modules.common.service;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.delegate.PickImageDelegate;

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
            ((queryCompoundOperationsCommand, dataPhotoAttachments) -> {
               List<String> exceptFilePaths = new ArrayList<>();
               exceptFilePaths.addAll(Queryable.from(dataPhotoAttachments).map(DataPhotoAttachment::getUrl).toList());
               exceptFilePaths.addAll(obtainMediaFilesPaths(queryCompoundOperationsCommand.getResult()));
               return exceptFilePaths;
            }))
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
