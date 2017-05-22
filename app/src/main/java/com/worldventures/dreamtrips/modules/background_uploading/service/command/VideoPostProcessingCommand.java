package com.worldventures.dreamtrips.modules.background_uploading.service.command;


import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.ImmutableVideoUploadStatus;
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.UploadVideoFileCommand;
import com.worldventures.dreamtrips.modules.background_uploading.util.FileSplitter;
import com.worldventures.dreamtrips.modules.background_uploading.util.UploadTimeEstimator;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class VideoPostProcessingCommand extends PostProcessingCommand<PostWithVideoAttachmentBody> {

   public static final int PROGRESS_URL_OBTAINED = 10;

   @Inject Janet janet;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;
   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;
   @Inject UploadTimeEstimator uploadTimeEstimator;

   private ActionPipe<UploadVideoFileCommand> uploadVideoFilePipe;

   public VideoPostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      super(postCompoundOperationModel);
   }

   @Override
   protected Observable<PostCompoundOperationModel<PostWithVideoAttachmentBody>> createMediaEntitiesIfNeeded(PostCompoundOperationModel<PostWithVideoAttachmentBody> postOperationModel) {
      uploadVideoFilePipe = janet.createPipe(UploadVideoFileCommand.class);
      return obtainUrlIfNeed(postCompoundOperationModel.body())
            .doOnNext(this::urlObtained)
            .flatMap(videoPostBody -> uploadVideoFilePipe.createObservable(new UploadVideoFileCommand(videoPostBody)))
            .flatMap(state -> {
               videoAttachmentUpdated(state.action.getVideoAttachmentBody(), state.progress);
               switch (state.status) {
                  case START:
                     startTimeEstimation();
                     return Observable.empty();
                  case SUCCESS:
                     pingAssetStatusInteractor.launchUpdatingVideoProcessingPipe().send(new LaunchUpdatingVideoProcessingCommand());
                     return Observable.just(postCompoundOperationModel);
                  case FAIL:
                     return Observable.error(state.exception);
                  default:
                     return Observable.empty();
               }
            });
   }

   private Observable<PostWithVideoAttachmentBody> obtainUrlIfNeed(PostWithVideoAttachmentBody videoBody) {
      //todo input valid code
      return Observable.just(postCompoundOperationModel.body());
//      if (videoBody.videoUploadStatus() != null) return Observable.just(postCompoundOperationModel.body());
//      int chunkCount = FileSplitter.computeChunkCount(new File(videoBody.videoPath()));
//
//      return janet.createPipe(RegisterVideoUploadCommand.class, Schedulers.io())
//            .createObservableResult(new RegisterVideoUploadCommand(chunkCount))
//            .map(RegisterVideoUploadCommand::getResult)
//            .map(registeredUrls -> ImmutableVideoUploadStatus.builder()
//                  .chunkPosition(0)
//                  .registeredUrls(registeredUrls)
//                  .chunkStatuses(new ArrayList<>())
//                  .build())
//            .map(videoFileStatus -> ImmutablePostWithVideoAttachmentBody.copyOf(videoBody)
//                  .withVideoUploadStatus(videoFileStatus));
   }

   private void urlObtained(PostWithVideoAttachmentBody videoBody) {
      postCompoundOperationModel = ImmutablePostCompoundOperationModel
            .copyOf(postCompoundOperationModel)
            .withProgress(PROGRESS_URL_OBTAINED)
            .withBody(videoBody);
      notifyCompoundCommandChanged(postCompoundOperationModel);
   }

   private void videoAttachmentUpdated(PostWithVideoAttachmentBody videoBody, int progress) {
      long remainingTimeInMillis = uploadTimeEstimator.estimate(progress, System.currentTimeMillis());
      double updatedAverageSpeed = uploadTimeEstimator.getAverageUploadSpeed();
      postCompoundOperationModel = compoundOperationObjectMutator.videoAttachmentChanged(postCompoundOperationModel,
            videoBody, progress, remainingTimeInMillis, updatedAverageSpeed);
      notifyCompoundCommandChanged(postCompoundOperationModel);
   }

   private void startTimeEstimation() {
      PostWithVideoAttachmentBody body = postCompoundOperationModel.body();
      File uploadingVideoFile = new File(body.videoPath());
      long totalSize = uploadingVideoFile.length();
      int totalUploadedSize = body.videoUploadStatus().chunkPosition() * FileSplitter.CHUNK_SIZE;

      uploadTimeEstimator.prepare(totalSize, totalUploadedSize, FileSplitter.CHUNK_SIZE,
            postCompoundOperationModel.averageUploadSpeed());
      uploadTimeEstimator.onUploadingStarted(System.currentTimeMillis());
   }

   @Override
   protected void cancel() {
      super.cancel();
      if (uploadVideoFilePipe != null) uploadVideoFilePipe.cancelLatest();
   }

   @Override
   protected void sendAnalytics() {
   }
}
