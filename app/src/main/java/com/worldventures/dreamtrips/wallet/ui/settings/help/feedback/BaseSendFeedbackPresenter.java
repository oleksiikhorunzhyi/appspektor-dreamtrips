package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.view.WindowManager;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.command.MediaAttachmentPrepareCommand;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.bundle.FeedbackImageAttachmentsBundle;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.CancelableFeedbackAttachmentsManager;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.UploadFeedbackAttachmentCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SendWalletFeedbackCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

public abstract class BaseSendFeedbackPresenter<S extends BaseSendFeedbackPresenter.Screen> extends WalletPresenter<S, Parcelable> {

   public static final int MAX_PHOTOS_ATTACHMENT = 5;

   @Inject protected Activity activity;
   @Inject FeedbackInteractor feedbackInteractor;
   @Inject WalletSettingsInteractor settingsInteractor;
   @Inject MediaInteractor mediaInteractor;
   @Inject Router router;

   protected final CancelableFeedbackAttachmentsManager attachmentsManager;
   private int attachmentsCount;

   public BaseSendFeedbackPresenter(Context context, Injector injector) {
      super(context, injector);
      attachmentsManager = new CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe());
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeAttachmentsPreparation();
      observeAttachments();
   }

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

   public void clearAttachments() {
      attachmentsManager.removeAll();
   }

   public List<FeedbackImageAttachment> getImagesAttachments() {
      return Queryable.from(attachmentsManager.getAttachments()).map(EntityStateHolder::entity).toList();
   }

   public void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   public void chosenAttachments() {
      getView().pickPhoto();
   }

   public void onRetryUploadingAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
      uploadImageAttachment(Uri.parse(holder.entity().getOriginalFilePath()));
   }

   private void uploadImageAttachment(Uri path) {
      attachmentsManager.send(path.toString());
   }

   public void onRemoveAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      removeAttachment(holder);
   }

   private void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder) {
      attachmentsManager.remove(holder);
      getView().removeAttachment(holder);
   }

   public void openFullScreenPhoto(EntityStateHolder<FeedbackImageAttachment> holder) {
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

   public void handleAttachedImages(List<BasePickerViewModel> models) {
      mediaInteractor.mediaAttachmentPreparePipe().send(new MediaAttachmentPrepareCommand(models));
   }

   public int getAttachmentsCount() {
      return attachmentsCount;
   }

   protected abstract void handleFailSentFeedback(SendWalletFeedbackCommand command, Throwable throwable);

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      attachmentsManager.cancelAll();
   }

   public interface Screen extends WalletScreen {

      void changeActionSendMenuItemEnabled(boolean enable);

      void removeAttachment(EntityStateHolder<FeedbackImageAttachment> holder);

      void changeAddPhotosButtonEnabled(boolean enable);

      void setAttachments(List<EntityStateHolder<FeedbackImageAttachment>> attachments);

      void updateAttachment(EntityStateHolder<FeedbackImageAttachment> updatedHolder);

      void pickPhoto();

      OperationView<SendWalletFeedbackCommand> provideOperationSendFeedback();
   }
}
