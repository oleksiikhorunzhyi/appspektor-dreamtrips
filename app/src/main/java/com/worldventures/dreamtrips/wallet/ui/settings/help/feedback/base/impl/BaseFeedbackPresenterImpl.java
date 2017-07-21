package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.impl;


import android.app.Activity;
import android.net.Uri;
import android.view.WindowManager;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.CancelableFeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.picker.command.MediaAttachmentPrepareCommand;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseFeedbackScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.base.BaseSendFeedbackPresenter;

import java.util.List;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

public abstract class BaseFeedbackPresenterImpl<S extends BaseFeedbackScreen> extends WalletPresenterImpl<S> implements BaseSendFeedbackPresenter<S>{

   public static final int MAX_PHOTOS_ATTACHMENT = 5;

   private final FeedbackInteractor feedbackInteractor;
   private final WalletSettingsInteractor settingsInteractor;
   private final MediaInteractor mediaInteractor;
   private final Router router;
   private final CancelableFeedbackAttachmentsManager attachmentsManager;

   private int attachmentsCount;

   public BaseFeedbackPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FeedbackInteractor feedbackInteractor,
         WalletSettingsInteractor walletSettingsInteractor, MediaInteractor mediaInteractor, Router router) {
      super(navigator, smartCardInteractor, networkService);
      this.feedbackInteractor = feedbackInteractor;
      this.settingsInteractor = walletSettingsInteractor;
      this.mediaInteractor = mediaInteractor;
      this.router = router;
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
                     for(Uri attachmentUri : command.getResult()) {
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

   public List<FeedbackImageAttachment> getImagesAttachments() {
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
      //TODO : try to get rid from dat sheeiiit
      NavigationConfig config = NavigationConfigBuilder.forActivity()
            .data(new FeedbackImageAttachmentsBundle(attachmentsManager.getAttachments().indexOf(holder),
                  getImagesAttachments()))
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .build();
      router.moveTo(Route.FEEDBACK_IMAGE_ATTACHMENTS, config);
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

   public CancelableFeedbackAttachmentsManager getAttachmentsManager() {
      return attachmentsManager;
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      attachmentsManager.cancelAll();
   }

   protected abstract void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable);
}
