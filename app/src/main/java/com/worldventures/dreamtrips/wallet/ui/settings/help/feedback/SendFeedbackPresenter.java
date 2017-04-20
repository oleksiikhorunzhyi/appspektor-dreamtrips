package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowManager;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.SendFeedbackCommand;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.infopages.presenter.SendFeedbackPresenter.PICKER_MAX_IMAGES;

public class SendFeedbackPresenter extends WalletPresenter<SendFeedbackPresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject FeedbackInteractor feedbackInteractor;

   private final FeedbackAttachmentsManager attachmentsManager;

   public SendFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
      attachmentsManager = new FeedbackAttachmentsManager();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      super.onSaveInstanceState(bundle);
      attachmentsManager.saveInstanceState(bundle);
   }

   @Override
   public void onRestoreInstanceState(Bundle instanceState) {
      super.onRestoreInstanceState(instanceState);
      attachmentsManager.restoreInstanceState(instanceState);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeFormValidation();
      observeAttachments();
      observePicker();
   }

   void fetchAttachments() {
      getView().setAttachments(attachmentsManager.getAttachments());
   }

   private void observePicker() {
      getView().observePickPhoto()
            .compose(bindView())
            .subscribe(this::uploadImageAttachment);
   }

   private void observeAttachments() {
      attachmentsManager.getAttachmentsObservable()
            .compose(bindView())
            .subscribe(holder -> {
               int attachmentsCount = attachmentsManager.getAttachments().size();
               getView().changeAddPhotosButtonEnabled(attachmentsCount < PICKER_MAX_IMAGES);
            });

      feedbackInteractor.attachmentsRemovedPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .doOnNext(result -> feedbackInteractor.attachmentsRemovedPipe().clearReplays())
            .subscribe(removedAttachments -> Queryable.from(attachmentsManager.getAttachments()).forEachR(holder -> {
               if (removedAttachments.contains(holder.entity())) {
                  attachmentsManager.remove(holder);
                  getView().removeAttachment(holder);
               }
            }));

      feedbackInteractor.uploadAttachmentPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationUploadAttachments())
                  .onProgress(this::updateImageAttachment)
                  .onSuccess(this::updateImageAttachment)
                  .onFail((failedCommand, throwable) -> updateImageAttachment(failedCommand))
                  .create()
            );
   }

   private void updateImageAttachment(UploadFeedbackAttachmentCommand command) {
      EntityStateHolder<FeedbackImageAttachment> updatedHolder = command.getEntityStateHolder();
      getView().updateAttachment(updatedHolder);
      attachmentsManager.update(updatedHolder);
   }

   private void observeFormValidation() {
      Observable.combineLatest(
            getView().getTextFeedbackMessage(),
            getView().getPhotoPickerVisibilityObservable(),
            this::validForm)
            .compose(bindView())
            .subscribe(enable -> getView().changeActionSendMenuItemEnabled(enable));
   }

   private boolean validForm(CharSequence textMessage, boolean pickerVisible) {
      boolean messageIsEmpty = textMessage.toString().trim().isEmpty();
      if (messageIsEmpty) return false;

      if (pickerVisible) return false;

      if (attachmentsManager.getFailedOrPendingAttachmentsCount() > 0) return false;

      return true;
   }

   public void goBack() {
      navigator.goBack();
   }

   void sendFeedback(String text) {
      getView().changeActionSendMenuItemEnabled(false);

      feedbackInteractor.sendFeedbackPipe()
            .createObservable(new SendFeedbackCommand(text, getImagesAttachments()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSendFeedback())
                  .onSuccess(command -> handleSuccessSentFeedback())
                  .onFail((command, throwable) -> getView().changeActionSendMenuItemEnabled(true))
                  .create()
            );
   }

   private void handleSuccessSentFeedback() {
      attachmentsManager.removeAll();
      goBack();
   }

   private List<FeedbackImageAttachment> getImagesAttachments() {
      return Queryable.from(attachmentsManager.getAttachments()).map(EntityStateHolder::entity).toList();
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   void chosenAttachments() {
      getView().pickPhoto();
   }

   void onRetryUploadingAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
      uploadImageAttachment(holder.entity().getOriginalFilePath());
   }

   private void uploadImageAttachment(String path) {
      FeedbackImageAttachment attachment = new FeedbackImageAttachment(path);
      feedbackInteractor.uploadAttachmentPipe().send(new UploadFeedbackAttachmentCommand(attachment));
   }

   void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
   }

   private void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      attachmentsManager.remove(holder);
      getView().removeAttachment(holder);
   }

   public interface Screen extends WalletScreen {

      void pickPhoto();

      void changeActionSendMenuItemEnabled(boolean enable);

      Observable<Boolean> getPhotoPickerVisibilityObservable();

      Observable<CharSequence> getTextFeedbackMessage();

      void showRetryUploadingUiForAttachment(EntityStateHolder<FeedbackImageAttachment> holder);

      void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder);

      void changeAddPhotosButtonEnabled(boolean enable);

      void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments);

      void updateAttachment(EntityStateHolder<FeedbackImageAttachment> updatedHolder);

      Observable<String> observePickPhoto();

      OperationView<UploadFeedbackAttachmentCommand> provideOperationUploadAttachments();

      OperationView<SendFeedbackCommand> provideOperationSendFeedback();
   }
}
