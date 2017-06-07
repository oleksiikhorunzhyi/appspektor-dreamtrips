package com.worldventures.dreamtrips.modules.background_uploading.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePhotoPostAction;
import com.worldventures.dreamtrips.modules.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@CommandAction
public class PostProcessingCommand extends Command<PostCompoundOperationModel> implements InjectableAction {

   private static final int DELAY_TO_DELETE_COMPOUND_OPERATION = 3;

   @Inject Janet janet;
   @Inject PostsInteractor postsInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   private PostCompoundOperationModel postCompoundOperationModel;

   private PublishSubject cancelationSubject = PublishSubject.create();

   private int compoundOperationDeletionDelay;

   public PostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      this.postCompoundOperationModel = postCompoundOperationModel;
      this.compoundOperationDeletionDelay = DELAY_TO_DELETE_COMPOUND_OPERATION;
   }

   public PostCompoundOperationModel getPostCompoundOperationModel() {
      return postCompoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      Observable.just(postCompoundOperationModel)
            .map(postOperationModel -> compoundOperationObjectMutator.start(postOperationModel))
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::createPhotosIfNeeded)
            .flatMap(this::createPost)
            // use trampoline for unit tests, works OK for usual scenario too
            .delay(compoundOperationDeletionDelay, TimeUnit.SECONDS, Schedulers.trampoline())
            .doOnNext(this::notifyCompoundCommandFinished)
            .compose(observeUntilCancel())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public void setCompoundOperationDeletionDelay(int compoundOperationDeletionDelay) {
      this.compoundOperationDeletionDelay = compoundOperationDeletionDelay;
   }

   private Observable.Transformer<PostCompoundOperationModel, PostCompoundOperationModel> observeUntilCancel() {
      return input -> input.takeUntil(cancelationSubject);
   }

   @Override
   protected void cancel() {
      super.cancel();
      cancelationSubject.onNext(null);
   }

   private Observable<PostCompoundOperationModel> createPhotosIfNeeded(PostCompoundOperationModel postOperationModel) {
      if (!postOperationModel.body().attachments().isEmpty()) return createPhotos(postOperationModel);
      return Observable.just(postOperationModel);
   }

   private Observable<PostCompoundOperationModel> createPhotos(PostCompoundOperationModel postOperationModel) {
      if (Queryable.from(postOperationModel.body().attachments())
            .all(attachment -> attachment.state() == PhotoAttachment.State.UPLOADED)) {
         return Observable.just(postOperationModel)
               .flatMap(this::createPhotosEntities);
      }
      final PostCompoundOperationModel[] tempOperationModels = {postOperationModel};
      return Observable.from(tempOperationModels[0].body().attachments())
            .filter(attachment -> attachment.state() != PhotoAttachment.State.UPLOADED)
            .concatMap(attachment -> janet.createPipe(PhotoAttachmentUploadingCommand.class)
                  .createObservable(new PhotoAttachmentUploadingCommand(tempOperationModels[0], attachment))
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
                  .doOnNext(postModel -> tempOperationModels[0] = postModel)
            )
            .last()
            .flatMap(this::createPhotosEntities);
   }

   private Observable<PostCompoundOperationModel> createPhotosEntities(PostCompoundOperationModel postOperationModel) {
      return postsInteractor.createPhotosPipe()
            .createObservableResult(new CreatePhotosCommand(postOperationModel.body()))
            .doOnNext(textualPost -> Timber.d("[New Post Creation] Photos created"))
            .map(Command::getResult)
            .map(photos -> compoundOperationObjectMutator.photosUploaded(postOperationModel, photos))
            .doOnNext(this::notifyCompoundCommandChanged);

   }

   private Observable<PostCompoundOperationModel> createPost(PostCompoundOperationModel postOperationModel) {
      return postsInteractor.createPostPipe()
            .createObservableResult(new CreatePostCommand(postOperationModel))
            .map(Command::getResult)
            .doOnNext(createdPost -> Timber.d("[New Post Creation] Post created"))
            .map(textualPost -> compoundOperationObjectMutator.finished(postOperationModel, textualPost))
            .doOnNext(this::notifyCompoundCommandChanged);
   }

   private void notifyCompoundCommandChanged(PostCompoundOperationModel postOperationModel) {
      Timber.d("[New Post Creation] Compound operation changed, %s", postOperationModel);
      postCompoundOperationModel = postOperationModel;
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(postOperationModel));
   }

   private void notifyCompoundCommandFinished(PostCompoundOperationModel postOperationModel) {
      Timber.d("[New Post Creation] Compound operation finished, %s", postOperationModel);
      copyCreatedAtFromUploadedPhotos(postOperationModel);
      postsInteractor.postCreatedPipe().send(new PostCreatedCommand(postOperationModel.body().createdPost()));
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandRemoved(postCompoundOperationModel));
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
      sendAnalytics();
   }

   /*
    * After creating textual post createdAt is missing in photo attachments
    * (there are complications to add createdAt in feed on server as well)
    */
   private void copyCreatedAtFromUploadedPhotos(PostCompoundOperationModel postOperationModel) {
      if (postOperationModel.body().uploadedPhotos() == null) return;
      for (Photo photo : postOperationModel.body().uploadedPhotos()) {
         List<Photo> addedPhotos = Queryable.from((postOperationModel.body().createdPost()).getAttachments())
               .filter(holder -> holder.getItem() instanceof Photo)
               .map(holder -> (Photo) holder.getItem())
               .toList();
         for (Photo addedPhoto : addedPhotos) {
            if (photo.equals(addedPhoto)) {
               addedPhoto.setCreatedAt(photo.getCreatedAt());
            }
         }
      }
   }

   private void sendAnalytics() {
      BaseAnalyticsAction action;
      if (postCompoundOperationModel.body().uploadedPhotos() == null) {
         action = SharePostAction.createPostAction(postCompoundOperationModel.body().createdPost());
      } else {
         action = SharePhotoPostAction.createPostAction(postCompoundOperationModel.body());
      }
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
