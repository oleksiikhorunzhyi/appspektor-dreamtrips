package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl;


import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.CancelableFeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.picker.command.MediaAttachmentPrepareCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

import java.util.List;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

public abstract class BaseFeedbackPresenterImpl<S extends BaseFeedbackScreen> extends WalletPresenterImpl<S> implements BaseSendFeedbackPresenter<S> {

   protected static final int MAX_PHOTOS_ATTACHMENT = 5;

   private final FeedbackInteractor feedbackInteractor;
   private final WalletSettingsInteractor settingsInteractor;
   private final MediaInteractor mediaInteractor;
   private final CancelableFeedbackAttachmentsManager attachmentsManager;

   private int attachmentsCount;

   public BaseFeedbackPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor, MediaInteractor mediaInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.feedbackInteractor = feedbackInteractor;
      this.settingsInteractor = walletSettingsInteractor;
      this.mediaInteractor = mediaInteractor;
      this.attachmentsManager = new CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe());
   }

   @Override
   public void attachView(S view) {
      super.attachView(view);
      observeAttachmentsPreparation();
      observeAttachments();
   }

   @Override
   public void fetchAttachments() {
      getView().setAttachments(attachmentsManager.getAttachments());
   }

   private void observeAttachmentsPreparation() {
      mediaInteractor.mediaAttachmentPreparePipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<MediaAttachmentPrepareCommand>()
                  .onSuccess(command -> {
                     for (Uri attachmentUri : command.getResult()) {
                        uploadImageAttachment(attachmentUri);
                     }
                  })
                  .onFail((command, throwable) -> Timber.e(throwable, "Cannot process attachments")));

   }

   private void observeAttachments() {
      attachmentsManager.getAttachmentsObservable()
            .compose(bindView())
            .subscribe(holder -> {
               this.attachmentsCount = attachmentsManager.getAttachments().size();
               getView().changeAddPhotosButtonEnabled(attachmentsCount < MAX_PHOTOS_ATTACHMENT);
            });

      feedbackInteractor.attachmentsRemovedPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(feedbackInteractor.attachmentsRemovedPipe()))
            .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
            .map(actionState -> actionState.action.getResult())
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

   @Override
   public void clearAttachments() {
      attachmentsManager.removeAll();
   }

   protected List<FeedbackImageAttachment> getImagesAttachments() {
      return Queryable.from(attachmentsManager.getAttachments()).map(EntityStateHolder::entity).toList();
   }

   @Override
   public void chosenAttachments() {
      getView().pickPhoto();
   }

   @Override
   public void onRetryUploadingAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
      uploadImageAttachment(Uri.parse(holder.entity().getOriginalFilePath()));
   }

   private void uploadImageAttachment(Uri path) {
      attachmentsManager.send(path.toString());
   }

   @Override
   public void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
   }

   private void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      attachmentsManager.remove(holder);
      getView().removeAttachment(holder);
   }

   @Override
   public void openFullScreenPhoto(EntityStateHolder<FeedbackImageAttachment> holder) {
      getNavigator().goFeedBackImageAttachments(attachmentsManager.getAttachments().indexOf(holder),
            getImagesAttachments());
   }

   protected abstract void handleSuccessSentFeedback();

   protected void sendFeedbackCommand(SendWalletFeedbackCommand feedbackCommand) {
      settingsInteractor.walletFeedbackPipe()
            .createObservable(feedbackCommand)
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSendFeedback())
                  .onSuccess(command -> handleSuccessSentFeedback())
                  .onFail(this::handleFailSentFeedback)
                  .create()
            );
   }

   @Override
   public void handleAttachedImages(List<PhotoPickerModel> models) {
      mediaInteractor.mediaAttachmentPreparePipe().send(new MediaAttachmentPrepareCommand(models));
   }

   @Override
   public int getAttachmentsCount() {
      return attachmentsCount;
   }

   protected CancelableFeedbackAttachmentsManager getAttachmentsManager() {
      return attachmentsManager;
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      attachmentsManager.cancelAll();
   }

   protected abstract void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable);
}
