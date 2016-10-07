package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.infopages.api.GetFeedbackReasonsQuery;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class SendFeedbackPresenter extends Presenter<SendFeedbackPresenter.View> {

   public static final int PICKER_REQUEST_ID = SendFeedbackPresenter.class.getSimpleName().hashCode();
   public static final int PICKER_MAX_IMAGES = 5;

   @Inject MediaPickerManager mediaPickerManager;
   @Inject SnappyRepository db;
   @Inject FeedbackInteractor feedbackInteractor;
   @Inject Router router;
   private FeedbackAttachmentsManager attachmentsManager = new FeedbackAttachmentsManager();

   @Override
   public void takeView(View view) {
      super.takeView(view);
      getFeedbackReasons(view);
      subscribeToFormValidation();
      subscribeToMediaPicker();
      subscribeToAttachments();
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
      doRequest(new GetFeedbackReasonsQuery(), feedbackTypes -> {
         db.setFeedbackTypes(feedbackTypes);
         view.setFeedbackTypes(feedbackTypes);
         view.hideProgressBar();
      }, spiceException -> {
         SendFeedbackPresenter.this.handleError(spiceException);
         view.setFeedbackTypes(db.getFeedbackTypes());
         view.hideProgressBar();
      });
      view.showProgressDialog();
   }

   public void sendFeedback(int feedbackType, String text) {
      view.changeDoneButtonState(false);

      feedbackInteractor.getSendFeedbackPipe()
            .createObservable(new SendFeedbackCommand(feedbackType, text, getImageAttachments()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<SendFeedbackCommand>()
               .onSuccess(sendFeedbackCommand -> {
                  attachmentsManager.removeAll();
                  view.feedbackSent();
               })
               .onFail((sendFeedbackCommand, throwable) -> {
                  view.changeDoneButtonState(true);
                  super.handleError(sendFeedbackCommand, throwable);
               }));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Form validation
   ///////////////////////////////////////////////////////////////////////////

   public void subscribeToFormValidation() {
      Observable.combineLatest(view.getFeedbackTypeSelectedObservable(),
            view.getMessageTextObservable(),
            attachmentsManager.getAttachmentsObservable().startWith(Observable.just(null)),
            this::validateForm)
      .compose(bindView())
      .subscribe(view::changeDoneButtonState);
   }

   private boolean validateForm(FeedbackType feedbackType, CharSequence message,
         EntityStateHolder<FeedbackImageAttachment> stateHolder) {
      boolean feedbackTypeSelected = feedbackType.getId() > 0;
      if (!feedbackTypeSelected) return false;

      boolean messageIsEmpty = message.toString().trim().isEmpty();
      if (messageIsEmpty) return false;

      if (attachmentsManager.getFailedOrPendingAttachmentsCount() > 0) return false;

      return true;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Media picker
   ///////////////////////////////////////////////////////////////////////////

   public void onShowMediaPicker() {
      view.showMediaPicker(PICKER_REQUEST_ID, PICKER_MAX_IMAGES - attachmentsManager.getAttachments().size());
   }

   private void subscribeToMediaPicker() {
      mediaPickerManager.toObservable()
            .filter(attachment -> attachment.requestId == PICKER_REQUEST_ID)
            .compose(bindView())
            .subscribe(mediaAttachment -> attachImages(mediaAttachment.chosenImages));
   }

   private void attachImages(List<PhotoGalleryModel> chosenImages) {
      if (chosenImages.size() == 0) return;
      Queryable.from(chosenImages).forEachR(chosenImage -> uploadImageAttachment(chosenImage.getThumbnailPath()));
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
      feedbackInteractor.getUploadAttachmentPipe().send(new UploadFeedbackAttachmentCommand(attachment));
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
         view.changeAddPhotosButtonState(attachmentsCount < PICKER_MAX_IMAGES ? true : false);
      });

      feedbackInteractor.getAttachmentsRemovedPipe()
            .observeSuccessWithReplay()
            .compose(bindViewToMainComposer())
            .map(Command::getResult)
            .doOnNext(result -> feedbackInteractor.getAttachmentsRemovedPipe().clearReplays())
            .subscribe(removedAttachments -> {
               Queryable.from(attachmentsManager.getAttachments()).forEachR(holder -> {
                  if (removedAttachments.contains(holder.entity())) {
                     attachmentsManager.remove(holder);
                     view.removeAttachment(holder);
                  }
               });
            });

      feedbackInteractor.getUploadAttachmentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadFeedbackAttachmentCommand>()
               .onProgress((commandInProgress, progress) -> updateImageAttachment(commandInProgress))
               .onSuccess(successCommand -> updateImageAttachment(successCommand))
               .onFail((failedCommand, throwable) -> {
                  updateImageAttachment(failedCommand);
                  this.handleError(failedCommand, throwable);
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

      void showProgressDialog();

      void hideProgressBar();

      void showMediaPicker(int requestId, int maxPhotos);

      void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments);

      void updateAttachment(EntityStateHolder<FeedbackImageAttachment> image);

      void removeAttachment(EntityStateHolder<FeedbackImageAttachment> image);

      void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> attachmentHolder);

      Observable<CharSequence> getMessageTextObservable();

      Observable<FeedbackType> getFeedbackTypeSelectedObservable();

      void changeDoneButtonState(boolean enable);

      void changeAddPhotosButtonState(boolean enable);
   }
}
