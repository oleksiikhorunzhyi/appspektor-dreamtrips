package com.worldventures.dreamtrips.modules.background_uploading.service;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

@CommandAction
public class PostProcessingCommand extends Command<PostCompoundOperationModel> implements InjectableAction {

   @Inject Janet janet;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   private PostCompoundOperationModel postCompoundOperationModel;

   private PublishSubject cancelationSubject = PublishSubject.create();

   public PostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      this.postCompoundOperationModel = postCompoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      Observable.just(postCompoundOperationModel)
            .map(postOperationModel -> compoundOperationObjectMutator.start(postOperationModel))
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::createPhotosIfNeeded)
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::createPost)
            .doOnNext(this::notifyCompoundCommandChanged)
            .doOnError(e -> notifyCompoundCommandChanged(compoundOperationObjectMutator.failed(postCompoundOperationModel)))
            .compose(observeUntilCancel())
            .subscribe(callback::onSuccess, callback::onFail);
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
      if (postOperationModel.body().attachments() != null) return createPhotos(postOperationModel);
      return Observable.just(postOperationModel);
   }

   private Observable<PostCompoundOperationModel> createPhotos(PostCompoundOperationModel postOperationModel) {
      final PostCompoundOperationModel[] tempOperationModels = {postOperationModel};
      if (Queryable.from(postOperationModel.body().attachments())
            .all(attachment -> attachment.state() == PhotoAttachment.State.UPLOADED)) {
         return Observable.just(postOperationModel)
               .flatMap(this::createPhotosEntities);
      }
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
            .map(photos -> compoundOperationObjectMutator.photosUploaded(postOperationModel, photos));
   }

   private Observable<PostCompoundOperationModel> createPost(PostCompoundOperationModel postOperationModel) {
      return postsInteractor.createPostPipe()
            .createObservableResult(new CreatePostCommand(postOperationModel.body()))
            .map(Command::getResult)
            .doOnNext(createdPost -> Timber.d("[New Post Creation] Post created"))
            .map(textualPost -> compoundOperationObjectMutator.finished(postOperationModel, textualPost));
   }

   private void notifyCompoundCommandChanged(PostCompoundOperationModel postOperationModel) {
      Timber.d("[New Post Creation] Compound operation changed, %s", postOperationModel);
      postCompoundOperationModel = postOperationModel;
      backgroundUploadingInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(postOperationModel));
   }
}
