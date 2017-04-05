package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http.AbortVideoUploadingHttpAction;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http.CompleteVideoUploadingHttpAction;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UploadVideoFileCommand extends Command<PostWithVideoAttachmentBody> implements InjectableAction {

   @Inject Janet janet;

   private PostWithVideoAttachmentBody videoAttachmentBody;

   public UploadVideoFileCommand(PostWithVideoAttachmentBody videoAttachmentBody) {
      this.videoAttachmentBody = videoAttachmentBody;
   }

   public PostWithVideoAttachmentBody getVideoAttachmentBody() {
      return videoAttachmentBody;
   }

   @Override
   protected void cancel() {
      super.cancel();
      janet.createPipe(AbortVideoUploadingHttpAction.class)
            .send(new AbortVideoUploadingHttpAction(videoAttachmentBody.videoUploadStatus()
                  .registeredUrls()
                  .getAbortUrl()));
   }

   @Override
   protected void run(CommandCallback<PostWithVideoAttachmentBody> callback) throws Throwable {
      janet.createPipe(UploadVideoFileChunksCommand.class)
            .createObservable(new UploadVideoFileChunksCommand(new File(videoAttachmentBody.videoPath()),
                  videoAttachmentBody.videoUploadStatus()))
            .flatMap(state -> {
               videoAttachmentBody = ImmutablePostWithVideoAttachmentBody.copyOf(videoAttachmentBody)
                     .withVideoUploadStatus(state.action.getVideoUploadStatus());
               switch (state.status) {
                  case SUCCESS:
                     return Observable.just(state.action.getResult());
                  case FAIL:
                     return Observable.error(state.exception);
                  case PROGRESS:
                     callback.onProgress(state.progress);
                  default:
                     return Observable.empty();
               }
            })
            .flatMap(videoUploadStatus -> completeUploading())
            .doOnNext(body -> videoAttachmentBody = body)
            .doOnError(e -> videoAttachmentBody = ImmutablePostWithVideoAttachmentBody.copyOf(videoAttachmentBody)
                  .withState(PostBody.State.FAILED))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<PostWithVideoAttachmentBody> completeUploading() {
      return janet.createPipe(CompleteVideoUploadingHttpAction.class)
            .createObservableResult(new CompleteVideoUploadingHttpAction(videoAttachmentBody.videoUploadStatus()
                  .registeredUrls()
                  .getCompleteUrl(),
                  videoAttachmentBody.videoUploadStatus().chunkStatuses()))
            .map(CompleteVideoUploadingHttpAction::getResponse)
            .map(url -> ImmutablePostWithVideoAttachmentBody.copyOf(videoAttachmentBody)
                  .withState(PostBody.State.UPLOADED)
                  .withRemoteURL(url));
   }

}
