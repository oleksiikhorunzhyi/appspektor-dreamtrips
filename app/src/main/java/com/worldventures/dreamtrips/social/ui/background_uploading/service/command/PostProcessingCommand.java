package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.analytics.SharePostAction;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreatePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;

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
public class PostProcessingCommand<T extends PostBody> extends Command<PostCompoundOperationModel<T>> implements InjectableAction {

   public static final int DELAY_TO_DELETE_COMPOUND_OPERATION = 3;

   @Inject Janet janet;
   @Inject PostsInteractor postsInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   PostCompoundOperationModel<T> postCompoundOperationModel;

   private PublishSubject cancelationSubject = PublishSubject.create();

   private int compoundOperationDeletionDelay;

   protected PostProcessingCommand(PostCompoundOperationModel postCompoundOperationModel) {
      this.postCompoundOperationModel = postCompoundOperationModel;
      this.compoundOperationDeletionDelay = DELAY_TO_DELETE_COMPOUND_OPERATION;
   }

   public static PostProcessingCommand createPostProcessing(PostCompoundOperationModel postCompoundOperationModel) {
      switch (postCompoundOperationModel.type()) {
         case PHOTO:
            return new PhotoPostProcessingCommand(postCompoundOperationModel);
         case VIDEO:
            return new VideoPostProcessingCommand(postCompoundOperationModel);
         case TEXT:
            return new PostProcessingCommand(postCompoundOperationModel);
         default:
            throw new IllegalArgumentException("Unknown type of post");
      }
   }

   public PostCompoundOperationModel getPostCompoundOperationModel() {
      return postCompoundOperationModel;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel<T>> callback) throws Throwable {
      Observable.just(postCompoundOperationModel)
            .map(postOperationModel -> compoundOperationObjectMutator.start(postOperationModel))
            .doOnNext(this::notifyCompoundCommandChanged)
            .flatMap(this::prepareCompoundOperation)
            .flatMap(postModel -> createPost((PostCompoundOperationModel<T>) postModel))
            // use trampoline for unit tests, works OK for usual scenario too
            .delay(compoundOperationDeletionDelay, TimeUnit.SECONDS, Schedulers.trampoline())
            .doOnNext(postModel -> notifyCompoundCommandFinished((PostCompoundOperationModel<T>) postModel))
            .compose(observeUntilCancel())
            .doOnError(throwable -> {
               notifyCompoundCommandChanged(compoundOperationObjectMutator.failed(postCompoundOperationModel));
               backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
            })
            .subscribe(postModel -> callback.onSuccess((PostCompoundOperationModel<T>) postModel),
                  throwable -> callback.onFail((Throwable) throwable));
   }

   public void setCompoundOperationDeletionDelay(int compoundOperationDeletionDelay) {
      this.compoundOperationDeletionDelay = compoundOperationDeletionDelay;
   }

   private Observable.Transformer<PostCompoundOperationModel<T>, PostCompoundOperationModel<T>> observeUntilCancel() {
      return input -> input.takeUntil(cancelationSubject);
   }

   @Override
   protected void cancel() {
      super.cancel();
      cancelationSubject.onNext(null);
   }

   protected Observable<PostCompoundOperationModel<T>> prepareCompoundOperation(PostCompoundOperationModel<T> postOperationModel) {
      return Observable.just(postOperationModel);
   }

   private Observable<PostCompoundOperationModel<T>> createPost(PostCompoundOperationModel<T> postOperationModel) {
      return postsInteractor.createPostPipe()
            .createObservableResult(new CreatePostCommand(postOperationModel))
            .map(Command::getResult)
            .doOnNext(createdPost -> Timber.d("[New Post Creation] Post created"))
            .map(textualPost -> finished(postOperationModel, textualPost))
            .doOnNext(this::notifyCompoundCommandChanged);
   }

   protected PostCompoundOperationModel<T> finished(PostCompoundOperationModel<T> postOperationModel, TextualPost textualPost) {
      return (PostCompoundOperationModel<T>) compoundOperationObjectMutator.finished(postOperationModel, textualPost);
   }

   void notifyCompoundCommandChanged(PostCompoundOperationModel postOperationModel) {
      Timber.d("[New Post Creation] Compound operation changed, %s", postOperationModel);
      postCompoundOperationModel = postOperationModel;
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(postOperationModel));
   }

   protected void notifyCompoundCommandFinished(PostCompoundOperationModel<T> postOperationModel) {
      Timber.d("[New Post Creation] Compound operation finished, %s", postOperationModel);
      postsInteractor.postCreatedPipe().send(new PostCreatedCommand(postOperationModel.body().createdPost()));
      compoundOperationsInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandRemoved(postCompoundOperationModel));
      backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
      sendAnalytics();
   }

   protected void sendAnalytics() {
      BaseAnalyticsAction action = SharePostAction.createPostAction(postCompoundOperationModel.body().createdPost());
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}
