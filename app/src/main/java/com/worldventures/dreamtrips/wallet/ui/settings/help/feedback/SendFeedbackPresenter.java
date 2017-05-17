package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.view.WindowManager;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.CancelableFeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.SettingsHelpInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackScreen.MAX_PHOTOS_ATTACHMENT;

public class SendFeedbackPresenter extends WalletPresenter<SendFeedbackPresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SettingsHelpInteractor settingsHelpInteractor;
   @Inject FeedbackInteractor feedbackInteractor;
   @Inject Router router;

   private final CancelableFeedbackAttachmentsManager attachmentsManager;

   public SendFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
      attachmentsManager = new CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe());
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
               getView().changeAddPhotosButtonEnabled(attachmentsCount < MAX_PHOTOS_ATTACHMENT);
            });

      feedbackInteractor.attachmentsRemovedPipe()
            .observeSuccessWithReplay()
            .doOnNext(command -> feedbackInteractor.attachmentsRemovedPipe().clearReplays())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(removedAttachments -> Queryable.from(attachmentsManager.getAttachments()).forEachR(holder -> {
               if (removedAttachments.contains(holder.entity())) {
                  attachmentsManager.remove(holder);
                  getView().removeAttachment(holder);
               }
            }));

      feedbackInteractor.uploadAttachmentPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(feedbackInteractor.uploadAttachmentPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<UploadFeedbackAttachmentCommand>()
                  .onStart(this::updateImageAttachment)
                  .onProgress((command, integer) -> updateImageAttachment(command))
                  .onSuccess(this::onCommandFinished)
                  .onFail((failedCommand, throwable) -> onCommandFinished(failedCommand))
            );
   }

   private void onCommandFinished(UploadFeedbackAttachmentCommand command) {
      updateImageAttachment(command);
      attachmentsManager.onCommandFinished(command);
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

   void sendFeedback(SendFeedbackPath.FeedbackType feedbackType, String text) {
      getView().changeActionSendMenuItemEnabled(false);

      settingsHelpInteractor.walletFeedbackPipe()
            .createObservable(feedbackType == SendFeedbackPath.FeedbackType.SmartCardFeedback ?
                  new SmartCardFeedbackCommand(text, getImagesAttachments()) :
                  new CustomerSupportFeedbackCommand(text, getImagesAttachments()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSendFeedback())
                  .onSuccess(command -> handleSuccessSentFeedback())
                  .onFail((command, throwable) -> getView().changeActionSendMenuItemEnabled(true))
                  .create()
            );
   }

   private void handleSuccessSentFeedback() {
      clearAttachments();
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
      uploadImageAttachment(Uri.parse(holder.entity().getOriginalFilePath()));
   }

   private void uploadImageAttachment(Uri path) {
      attachmentsManager.send(path.toString());
   }

   void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
   }

   private void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      attachmentsManager.remove(holder);
      getView().removeAttachment(holder);
   }

   void openFullScreenPhoto(EntityStateHolder<FeedbackImageAttachment> holder) {
      NavigationConfig config = NavigationConfigBuilder.forActivity()
            .data(new FeedbackImageAttachmentsBundle(attachmentsManager.getAttachments().indexOf(holder),
                  getImagesAttachments()))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build();
      router.moveTo(Route.FEEDBACK_IMAGE_ATTACHMENTS, config);
   }

   void clearAttachments() {
      attachmentsManager.removeAll();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      attachmentsManager.cancelAll();
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

      Observable<Uri> observePickPhoto();

      OperationView<SendWalletFeedbackCommand> provideOperationSendFeedback();
   }
}
