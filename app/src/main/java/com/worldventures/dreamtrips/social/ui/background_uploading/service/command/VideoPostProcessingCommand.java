package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.ShareVideoPostAction;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateVideoCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class VideoPostProcessingCommand extends PostProcessingCommand<PostWithVideoAttachmentBody> {

   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;

   private ActionPipe<UploadVideoFileCommand> uploadVideoFileCommandActionPipe;

   public VideoPostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      super(postCompoundOperationModel);
   }

   @Override
   protected Observable<PostCompoundOperationModel<PostWithVideoAttachmentBody>> prepareCompoundOperation(PostCompoundOperationModel<PostWithVideoAttachmentBody> postOperationModel) {
      return Observable.just(postOperationModel)
            .flatMap(postCompoundOperationModel -> {
               if (postCompoundOperationModel.body().videoUid() != null) {
                  return Observable.just(postCompoundOperationModel);
               } else if (postCompoundOperationModel.body().uploadId() != null) {
                  return createVideoEntity(postCompoundOperationModel);
               } else {
                  return uploadVideoToMicrosevice(postOperationModel).flatMap(this::createVideoEntity);
               }
            });
   }

   private Observable<PostCompoundOperationModel<PostWithVideoAttachmentBody>> uploadVideoToMicrosevice(PostCompoundOperationModel<PostWithVideoAttachmentBody> postOperationModel) {
      uploadVideoFileCommandActionPipe = janet.createPipe(UploadVideoFileCommand.class);
      return uploadVideoFileCommandActionPipe
            .createObservable(new UploadVideoFileCommand(postOperationModel))
            .flatMap(state -> {
               notifyCompoundCommandChanged(state.action.getPostCompoundOperationModel());
               switch (state.status) {
                  case SUCCESS:
                     return Observable.just(state.action.getResult().body());
                  case FAIL:
                     return Observable.error(state.exception);
                  default:
                     return Observable.empty();
               }
            })
            .doOnNext(body -> sendAnalytics())
            .map(updateBody -> compoundOperationObjectMutator.videoUploaded(postOperationModel, updateBody))
            .doOnNext(this::notifyCompoundCommandChanged);
   }

   private Observable<PostCompoundOperationModel<PostWithVideoAttachmentBody>> createVideoEntity(PostCompoundOperationModel<PostWithVideoAttachmentBody> postOperationModel) {
      return postsInteractor.createVideoPipe()
            .createObservableResult(new CreateVideoCommand(postOperationModel.body().uploadId()))
            .doOnNext(textualPost -> Timber.d("[New Post Creation] Videos created"))
            .map(Command::getResult)
            .map(uid -> compoundOperationObjectMutator.videoCreated(postOperationModel, uid))
            .doOnNext(this::notifyCompoundCommandChanged);
   }

   @Override
   protected void cancel() {
      super.cancel();
      if (uploadVideoFileCommandActionPipe != null) {
         uploadVideoFileCommandActionPipe.cancelLatest();
      }
   }

   @Override
   protected void notifyCompoundCommandFinished(PostCompoundOperationModel<PostWithVideoAttachmentBody> postOperationModel) {
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
      pingAssetStatusInteractor.launchUpdatingVideoProcessingPipe().send(new LaunchUpdatingVideoProcessingCommand());
   }

   @Override
   protected void sendAnalytics() {
      BaseAnalyticsAction action = ShareVideoPostAction.createPostAction(postCompoundOperationModel.body());
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
