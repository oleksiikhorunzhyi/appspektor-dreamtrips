package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.SendFeedbackAnalyticAction;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SendFeedbackPresenter extends Presenter<SendFeedbackPresenter.View> {

   private static final int PICKER_MAX_IMAGES = 5;

   @Inject FeedbackInteractor feedbackInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Router router;

   private FeedbackAttachmentsManager attachmentsManager = new FeedbackAttachmentsManager();

   @Override
   public void takeView(View view) {
      super.takeView(view);
      getFeedbackReasons(view);
      subscribeToFormValidation();
      subscribeToAttachments();
   }

   @Override
   public void onStart() {
      super.onStart();
      subscribeToUploadingAttachments();
   }

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      attachmentsManager.restoreInstanceState(savedState);
   }

   @Override
   public void saveInstanceState(Bundle savedState) {
      super.saveInstanceState(savedState);
      attachmentsManager.saveInstanceState(savedState);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getting feedback reasons and sending feedback
   ///////////////////////////////////////////////////////////////////////////

   private void getFeedbackReasons(View view) {
      feedbackInteractor.getFeedbackPipe()
            .createObservable(new GetFeedbackCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFeedbackCommand>()
                  .onStart(action -> {
                     List<FeedbackType> items = action.items();
                     view.setFeedbackTypes(action.items());
                     if (items == null || items.isEmpty()) {
                        view.showProgressBar();
                     }
                  })
                  .onSuccess(action -> {
                     view.setFeedbackTypes(action.items());
                     view.hideProgressBar();
                  })
                  .onFail((action, e) -> {
                     handleError(action, e);
                     view.setFeedbackTypes(action.items());
                     view.hideProgressBar();
                  }));
   }

   public void sendFeedback(int feedbackType, String text) {
      analyticsInteractor.analyticsActionPipe()
            .send(new SendFeedbackAnalyticAction(feedbackType, getImageAttachments().size()));
      view.changeDoneButtonState(false);

      feedbackInteractor.sendFeedbackPipe()
            .createObservable(new SendFeedbackCommand(feedbackType, text, getImageAttachments()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<SendFeedbackCommand>()
                  .onSuccess(sendFeedbackCommand -> {
                     attachmentsManager.removeAll();
                     view.feedbackSent();
                  })
                  .onFail((sendFeedbackCommand, throwable) -> {
                     view.changeDoneButtonState(true);
                     handleError(sendFeedbackCommand, throwable);
                  }));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Form validation
   ///////////////////////////////////////////////////////////////////////////

   public void subscribeToFormValidation() {
      Observable.combineLatest(view.getFeedbackTypeSelectedObservable(),
            view.getMessageTextObservable(),
            view.getPhotoPickerVisibilityObservable(),
            attachmentsManager.getAttachmentsObservable().startWith(Observable.just(null)),
            this::validateForm)
            .compose(bindView())
            .subscribe(view::changeDoneButtonState);
   }

   private boolean validateForm(FeedbackType feedbackType, CharSequence message,
         boolean photoPickerVisible, EntityStateHolder<FeedbackImageAttachment> stateHolder) {
      boolean feedbackTypeSelected = feedbackType.getId() > 0;
      if (!feedbackTypeSelected) return false;

      boolean messageIsEmpty = message.toString().trim().isEmpty();
      if (messageIsEmpty) return false;

      if (photoPickerVisible) return false;

      if (attachmentsManager.getFailedOrPendingAttachmentsCount() > 0) return false;

      return true;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Media picker
   ///////////////////////////////////////////////////////////////////////////

   public void onShowMediaPicker() {
      view.showMediaPicker(PICKER_MAX_IMAGES - attachmentsManager.getAttachments().size());
   }

   public void imagesPicked(MediaPickerAttachment mediaPickerAttachment) {
      Queryable.from(mediaPickerAttachment.getChosenImages())
            .map(pickerModel -> WalletFilesUtils.convertPickedPhotoToUri(pickerModel).toString())
            .forEachR(this::uploadImageAttachment);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Attachments
   ///////////////////////////////////////////////////////////////////////////

   public void onFeedbackAttachmentClicked(EntityStateHolder<FeedbackImageAttachment> holder) {
      EntityStateHolder.State state = holder.state();
      switch (state) {
         case DONE:
         case PROGRESS:
            NavigationConfig config = NavigationConfigBuilder.forActivity()
                  .data(new FeedbackImageAttachmentsBundle(attachmentsManager.getAttachments().indexOf(holder),
                        getImageAttachments()))
                  .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                  .build();
            router.moveTo(Route.FEEDBACK_IMAGE_ATTACHMENTS, config);
            break;
         case FAIL:
            view.showRetryUploadingUiForAttachment(holder);
            break;
      }
   }

   private void uploadImageAttachment(String path) {
      FeedbackImageAttachment attachment = new FeedbackImageAttachment(path);
      feedbackInteractor.uploadAttachmentPipe().send(new UploadFeedbackAttachmentCommand(attachment));
   }

   public void onRetryUploadingAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
      uploadImageAttachment(holder.entity().getOriginalFilePath());
   }

   public void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
   }

   private void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      attachmentsManager.remove(holder);
      view.removeAttachment(holder);
   }

   private void subscribeToAttachments() {
      view.setAttachments(attachmentsManager.getAttachments());

      attachmentsManager.getAttachmentsObservable()
            .compose(bindView())
            .subscribe(holder -> {
               int attachmentsCount = attachmentsManager.getAttachments().size();
               view.changeAddPhotosButtonState(attachmentsCount < PICKER_MAX_IMAGES);
            });

      feedbackInteractor.attachmentsRemovedPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(feedbackInteractor.attachmentsRemovedPipe()))
            .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
            .map(actionState -> actionState.action.getResult())
            .compose(bindViewToMainComposer())
            .subscribe(removedAttachments -> {
               Queryable.from(attachmentsManager.getAttachments()).forEachR(holder -> {
                  if (removedAttachments.contains(holder.entity())) {
                     attachmentsManager.remove(holder);
                     view.removeAttachment(holder);
                  }
               });
            });
   }

   private void subscribeToUploadingAttachments() {
      feedbackInteractor.uploadAttachmentPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(feedbackInteractor.uploadAttachmentPipe()))
            .compose(bindUntilStop())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<UploadFeedbackAttachmentCommand>()
                  .onProgress((commandInProgress, progress) -> updateImageAttachment(commandInProgress))
                  .onSuccess(this::updateImageAttachment)
                  .onFail((failedCommand, throwable) -> {
                     updateImageAttachment(failedCommand);
                     handleError(failedCommand, throwable);
                  })
            );
   }

   private void updateImageAttachment(UploadFeedbackAttachmentCommand command) {
      EntityStateHolder<FeedbackImageAttachment> updatedHolder = command.getEntityStateHolder();
      view.updateAttachment(updatedHolder);
      attachmentsManager.update(updatedHolder);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helpers
   ///////////////////////////////////////////////////////////////////////////

   private List<FeedbackImageAttachment> getImageAttachments() {
      return Queryable.from(attachmentsManager.getAttachments()).map(EntityStateHolder::entity).toList();
   }

   public interface View extends Presenter.View {
      void setFeedbackTypes(List<FeedbackType> feedbackTypes);

      void feedbackSent();

      void showProgressBar();

      void hideProgressBar();

      void showMediaPicker(int maxPhotos);

      void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments);

      void updateAttachment(EntityStateHolder<FeedbackImageAttachment> image);

      void removeAttachment(EntityStateHolder<FeedbackImageAttachment> image);

      void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder);

      Observable<CharSequence> getMessageTextObservable();

      Observable<FeedbackType> getFeedbackTypeSelectedObservable();

      Observable<Boolean> getPhotoPickerVisibilityObservable();

      void changeDoneButtonState(boolean enable);

      void changeAddPhotosButtonState(boolean enable);
   }
}
