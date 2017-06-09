package com.worldventures.dreamtrips.modules.background_uploading.service.command.video;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.VideoMicroserviceModule;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessStatus;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.PostProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http.CheckVideoProcessingHttpAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@CommandAction
public class UpdateVideoProcessStatusCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(VideoMicroserviceModule.JANET_QUALIFIER) Janet janet;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   private List<PostCompoundOperationModel> operationModels;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      compoundOperationsInteractor.compoundOperationsPipe()
            .createObservableResult(new QueryCompoundOperationsCommand())
            .map(command -> {
               operationModels = command.getResult();
               return Queryable.from(command.getResult())
                     .filter(element -> element.state() == CompoundOperationState.PROCESSING)
                     .map(element -> {
                        PostWithVideoAttachmentBody postWithVideoAttachmentBody = (PostWithVideoAttachmentBody) element.body();
                        return postWithVideoAttachmentBody.uploadId();
                     })
                     .toList();
            })
            .flatMap(ids -> janet.createPipe(CheckVideoProcessingHttpAction.class, Schedulers.io())
                  .createObservableResult(new CheckVideoProcessingHttpAction(ids)))
            .map(httpAction -> httpAction.getBunchStatus().getVideoProcessStatuses())
            .doOnNext(this::updateOperationItems)
            .subscribe(videoProcessBunchStatus -> callback.onSuccess(null), callback::onFail);
   }


   private void updateOperationItems(List<VideoProcessStatus> videoProcessStatuses) {
      Observable.from(operationModels)
            .flatMap(operationModel -> {
                     VideoProcessStatus videoProcessStatus = getProcessStatusForModel(videoProcessStatuses, operationModel);
                     if (videoProcessStatus == null ||
                           videoProcessStatus.getAssetStatus().equals(VideoProcessStatus.STATUS_COMPLETED)) {
                        return Observable.just(operationModel)
                              .map(compoundOperationObjectMutator::finishedEmpty)
                              .doOnNext(this::notifyCompoundOperationChanged)
                              .delay(PostProcessingCommand.DELAY_TO_DELETE_COMPOUND_OPERATION,
                                    TimeUnit.SECONDS, Schedulers.trampoline())
                              .doOnNext(this::notifyCompoundOperationRemoved);
                     } else if (videoProcessStatus.getAssetStatus().equals(VideoProcessStatus.STATUS_ERROR)) {
                        return Observable.just(operationModel)
                              .map(compoundOperationObjectMutator::failed)
                              .doOnNext(this::notifyCompoundOperationChanged);
                     } else return Observable.empty();
                  }
            ).subscribe(Actions.empty(), e -> Timber.e("Failed to perform update", e));
   }

   private VideoProcessStatus getProcessStatusForModel(List<VideoProcessStatus> processStatuses, PostCompoundOperationModel operationModel) {
      return Queryable.from(processStatuses)
            .firstOrDefault(tempStatus -> {
               PostWithVideoAttachmentBody postWithVideoAttachmentBody = (PostWithVideoAttachmentBody) operationModel.body();
               return tempStatus.getAssetId().equals(postWithVideoAttachmentBody.uploadId());
            });
   }

   private void notifyCompoundOperationChanged(PostCompoundOperationModel postModel) {
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(postModel));
   }

   private void notifyCompoundOperationRemoved(PostCompoundOperationModel postModel) {
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandRemoved(postModel));
   }
}
