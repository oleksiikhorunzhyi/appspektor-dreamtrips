package com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.api.post.CheckVideoProcessingHttpAction;
import com.worldventures.dreamtrips.api.post.model.response.PostStatus;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.PostProcessingCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@CommandAction
public class UpdateVideoProcessStatusCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
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
                        return postWithVideoAttachmentBody.createdPost().getUid();
                     })
                     .toList();
            })
            .flatMap(ids -> {
               if (ids.isEmpty()) return Observable.just(new PostStatus[0]);
               return janet.createPipe(CheckVideoProcessingHttpAction.class, Schedulers.io())
                     .createObservableResult(new CheckVideoProcessingHttpAction(ids.toArray(new String[0])))
                     .map(httpAction -> httpAction.response().postStatuses());
            })
            .doOnNext(this::updateOperationItems)
            .subscribe(status -> callback.onSuccess(null), callback::onFail);
   }

   private void updateOperationItems(PostStatus[] postStatuses) {
      Observable.from(operationModels)
            .flatMap(operationModel -> {
               TextualPost post = operationModel.body().createdPost();
               if (post == null) return Observable.empty();

               PostStatus postStatus = getPostStatus(post.getUid(), postStatuses);
               if (postStatus == null) return Observable.empty();

               return updateModelStatus(operationModel, postStatus);
            }).subscribe(Actions.empty(), e -> Timber.e(e, "Failed to perform update"));
   }

   private PostStatus getPostStatus(String postUid, PostStatus[] postStatuses) {
      return Queryable.from(postStatuses).firstOrDefault(postStatus -> postStatus.uid().equals(postUid));
   }

   private Observable<PostCompoundOperationModel> updateModelStatus(PostCompoundOperationModel model, PostStatus status) {
      if (status.status() == PostStatus.Status.COMPLETED) {
         return Observable.just(compoundOperationObjectMutator.finishedEmpty(model))
               .doOnNext(this::notifyCompoundOperationChanged)
               .delay(PostProcessingCommand.DELAY_TO_DELETE_COMPOUND_OPERATION,
                     TimeUnit.SECONDS, Schedulers.trampoline())
               .doOnNext(this::notifyCompoundOperationRemoved);
      } else if (status.status() == PostStatus.Status.FAILED) {
         return Observable.just(compoundOperationObjectMutator.failedProcessing(model))
               .doOnNext(this::notifyCompoundOperationChanged);
      } else {
         return Observable.empty();
      }
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
