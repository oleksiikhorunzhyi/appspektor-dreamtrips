package com.worldventures.dreamtrips.modules.background_uploading.service;


import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePhotosCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreatePostCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class PostProcessingCommand extends Command<PostCompoundOperationModel> implements InjectableAction {

   public static final int PROGRESS_PHOTOS_CREATING = 90;
   public static final int PROGRESS_PHOTOS_CREATED = 95;
   public static final int PROGRESS_POST_CREATED = 100;

   @Inject Janet janet;
   @Inject PostsInteractor postsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject SessionHolder<UserSession> sessionSessionHolder;

   private PostCompoundOperationModel postCompoundOperationModel;

   public PostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      this.postCompoundOperationModel = postCompoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      Observable.just(postCompoundOperationModel)
            .map(postOperationModel -> ImmutablePostCompoundOperationModel.copyOf(postOperationModel)
                  .withState(CompoundOperationState.STARTED))
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::createPhotosIfNeeded)
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::createPost)
            .doOnNext(this::notifyCompoundCommandChanged)
            .doOnNext(createdPost -> Timber.d("[New Post Creation] Post created"))
            .doOnError(e -> notifyCompoundCommandChanged(ImmutablePostCompoundOperationModel
                  .copyOf(postCompoundOperationModel)
                  .withState(CompoundOperationState.FAILED)))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<PostCompoundOperationModel> createPhotosIfNeeded(PostCompoundOperationModel postOperationModel) {
      if (postOperationModel.body().attachments() != null) return createPhotos(postOperationModel);
      return Observable.just(postOperationModel);
   }

   private Observable<PostCompoundOperationModel> createPhotos(PostCompoundOperationModel postOperationModel) {
      final PostCompoundOperationModel[] tempOperationModels = {postOperationModel};
      return Observable.from(tempOperationModels[0].body().attachments())
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
            .map(photos -> ImmutablePostCompoundOperationModel
                  .copyOf(postOperationModel)
                  .withProgress(PROGRESS_PHOTOS_CREATED)
                  .withBody(ImmutablePostWithAttachmentBody
                        .copyOf(postOperationModel.body())
                        .withUploadedPhotos(photos)));
   }

   private Observable<PostCompoundOperationModel> createPost(PostCompoundOperationModel postOperationModel) {
      return postsInteractor.createPostPipe()
            .createObservableResult(new CreatePostCommand(postOperationModel.body()))
            .map(Command::getResult)
            .map(textualPost -> {
               textualPost.setOwner(sessionSessionHolder.get().get().getUser());
               return ImmutablePostCompoundOperationModel
                     .copyOf(postOperationModel)
                     .withProgress(PROGRESS_POST_CREATED)
                     .withState(CompoundOperationState.FINISHED)
                     .withBody(ImmutablePostWithAttachmentBody
                           .copyOf(postOperationModel.body())
                           .withCreatedPost(textualPost));
            });
   }

   private void notifyCompoundCommandChanged(PostCompoundOperationModel postOperationModel) {
      Timber.d("[New Post Creation] Compound operation changed, %s", postOperationModel);
      postCompoundOperationModel = postOperationModel;
      backgroundUploadingInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(postOperationModel));
   }
}
