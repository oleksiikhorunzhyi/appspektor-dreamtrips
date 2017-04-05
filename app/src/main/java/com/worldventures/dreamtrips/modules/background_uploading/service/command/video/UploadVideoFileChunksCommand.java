package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.ChunkUploadingStatus;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.ImmutableChunkUploadingStatus;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.ImmutableVideoUploadStatus;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoChunkUploadUrl;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoUploadStatus;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.VideoPostProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http.UploadVideoChunkHttpAction;
import com.worldventures.dreamtrips.modules.background_uploading.util.FileSplitter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class UploadVideoFileChunksCommand extends Command<VideoUploadStatus> implements InjectableAction {

   public static final int PROGRESS_CHUNK_UPLOADING = 80;

   @Inject Janet janet;
   @Inject FileSplitter fileSplitter;

   private final File videoFile;
   private VideoUploadStatus videoUploadStatus;

   private int currentProgress = VideoPostProcessingCommand.PROGRESS_URL_OBTAINED;
   private double progressDelta = 0.0;

   private ActionPipe<UploadVideoChunkHttpAction> uploadVideoChunkHttpActionPipe;

   public UploadVideoFileChunksCommand(File videoFile, VideoUploadStatus videoUploadStatus) {
      this.videoFile = videoFile;
      this.videoUploadStatus = videoUploadStatus;
   }

   @Override
   protected void run(CommandCallback<VideoUploadStatus> callback) throws Throwable {
      uploadVideoChunkHttpActionPipe = janet.createPipe(UploadVideoChunkHttpAction.class, Schedulers.io());

      List<File> chunks = fileSplitter.split(videoFile, videoUploadStatus.chunkPosition());

      progressDelta = PROGRESS_CHUNK_UPLOADING / chunks.size();

      Observable.from(chunks)
            .concatMap(chunk -> {
               VideoChunkUploadUrl registeredUrl = Queryable.from(videoUploadStatus.registeredUrls().getChunkUrls())
                     .firstOrDefault(element -> element.id() == videoUploadStatus.chunkPosition() + 1);
               UploadVideoChunkHttpAction action = null;
               try {
                  action = new UploadVideoChunkHttpAction(registeredUrl.url(), chunk);
               } catch (IOException e) {
                  chunk.delete();
                  return Observable.error(e);
               }
               return uploadVideoChunkHttpActionPipe
                     .createObservable(action)
                     .flatMap(actionState -> {
                        switch (actionState.status) {
                           case SUCCESS:
                              chunk.delete();
                              currentProgress = (int) ((double) currentProgress + progressDelta);
                              callback.onProgress(currentProgress);
                              return Observable.just(actionState.action);
                           case FAIL:
                              chunk.delete();
                              return Observable.error(actionState.exception);
                           case PROGRESS:
                           default:
                              return Observable.empty();
                        }
                     })
                     .map(uploadVideoChunkHttpAction -> ImmutableChunkUploadingStatus.builder()
                           .eTag(uploadVideoChunkHttpAction.getETag())
                           .chunkNumber(videoUploadStatus.chunkPosition())
                           .build())
                     .map(chunkStatus -> {
                        updateVideoFileUploadStatus(chunkStatus);
                        return videoUploadStatus;
                     });
            })
            .last()
            .subscribe(callback::onSuccess, throwable -> {
               for (File chunk : chunks) chunk.delete();
               callback.onFail(throwable);
            });
   }

   private void updateVideoFileUploadStatus(ChunkUploadingStatus chunkUploadingStatus) {
      List<ChunkUploadingStatus> chunkUploadingStatuses = new ArrayList<>();
      chunkUploadingStatuses.addAll(videoUploadStatus.chunkStatuses());
      chunkUploadingStatuses.add(chunkUploadingStatus);

      videoUploadStatus = ImmutableVideoUploadStatus.copyOf(videoUploadStatus)
            .withChunkStatuses(chunkUploadingStatuses);

      videoUploadStatus = ImmutableVideoUploadStatus.copyOf(videoUploadStatus)
            .withChunkPosition(chunkUploadingStatus.chunkNumber() + 1);
   }

   public VideoUploadStatus getVideoUploadStatus() {
      return videoUploadStatus;
   }
}
