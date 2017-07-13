package com.worldventures.dreamtrips.modules.background_uploading.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePhotoPostAction;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class PhotoPostProcessingCommand extends PostProcessingCommand<PostWithPhotoAttachmentBody> {

   private PostCompoundOperationModel<PostWithPhotoAttachmentBody> tempOperationModel;

   private ActionPipe<PhotoAttachmentUploadingCommand> actionPipe;

   public PhotoPostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      super(postCompoundOperationModel);
   }

   @Override
   protected Observable<PostCompoundOperationModel<PostWithPhotoAttachmentBody>> prepareCompoundOperation(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postOperationModel) {
      return createPhotos(postOperationModel);
   }

   private Observable<PostCompoundOperationModel<PostWithPhotoAttachmentBody>> createPhotos(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postOperationModel) {
      actionPipe = janet.createPipe(PhotoAttachmentUploadingCommand.class);
      if (Queryable.from(postOperationModel.body().attachments())
            .all(attachment -> attachment.state() == PostBody.State.UPLOADED)) {
         return Observable.just(postOperationModel)
               .flatMap(this::createPhotosEntities);
      }
      tempOperationModel = postOperationModel;
      return Observable.from(tempOperationModel.body().attachments())
            .filter(attachment -> attachment.state() != PostBody.State.UPLOADED)
            .concatMap(attachment -> actionPipe
                  .createObservable(new PhotoAttachmentUploadingCommand(tempOperationModel, attachment))
                  .flatMap(state -> {
                     notifyCompoundCommandChanged(state.action.getPostCompoundOperationModel());
                     switch (state.status) {
                        case SUCCESS:
                           return Observable.just(state.action.getPostCompoundOperationModel());
                        case FAIL:
                           return Observable.error(state.exception);
                        default:
                           return Observable.empty();
                     }
                  })
                  .doOnNext(postModel -> Timber.d("[New Post Creation] Photo uploaded %s", postModel.toString()))
                  .doOnNext(postModel -> tempOperationModel = postModel)
            )
            .last()
            .flatMap(this::createPhotosEntities);
   }

   @Override
   protected void cancel() {
      super.cancel();
      if (actionPipe != null) actionPipe.cancelLatest();
   }

   private Observable<PostCompoundOperationModel<PostWithPhotoAttachmentBody>> createPhotosEntities(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postOperationModel) {
      return postsInteractor.createPhotosPipe()
            .createObservableResult(new CreatePhotosCommand(postOperationModel.body()))
            .doOnNext(textualPost -> Timber.d("[New Post Creation] Photos created"))
            .map(Command::getResult)
            .map(photos -> compoundOperationObjectMutator.photosUploaded(postOperationModel, photos))
            .doOnNext(this::notifyCompoundCommandChanged);
   }

   @Override
   protected void notifyCompoundCommandFinished(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postOperationModel) {
      super.notifyCompoundCommandFinished(postOperationModel);
      copyCreatedAtFromUploadedPhotos(postOperationModel.body());
   }

   /*
    * After creating textual post createdAt is missing in photo attachments
    * (there are complications to add createdAt in feed on server as well)
    */
   private void copyCreatedAtFromUploadedPhotos(PostWithPhotoAttachmentBody body) {
      if (body.uploadedPhotos() == null) return;
      try {
         for (Photo photo : body.uploadedPhotos()) {
            List<Photo> addedPhotos = Queryable.from((body.createdPost()).getAttachments())
                  .filter(holder -> holder.getItem() instanceof Photo)
                  .map(holder -> (Photo) holder.getItem())
                  .toList();
            for (Photo addedPhoto : addedPhotos) {
               if (photo.equals(addedPhoto)) {
                  addedPhoto.setCreatedAt(photo.getCreatedAt());
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected void sendAnalytics() {
      BaseAnalyticsAction action = SharePhotoPostAction.createPostAction(postCompoundOperationModel.body());
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
