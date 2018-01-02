package com.messenger.ui.presenter;

import com.messenger.entities.PhotoAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.ui.module.flagging.FlaggingPresenter;
import com.messenger.ui.module.flagging.FlaggingView;
import com.messenger.ui.util.chat.ChatTimestampFormatter;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand;

import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class MessageImageFullscreenPresenter extends Presenter<MessageImageFullscreenPresenter.View> {

   @ForActivity @Inject Injector injector;
   @Inject MessageDAO messageDAO;
   @Inject TripImagesInteractor tripImagesInteractor;
   private ChatTimestampFormatter timestampFormatter;
   private FlaggingPresenter flaggingPresenter;

   private PhotoAttachment photoAttachment;

   public MessageImageFullscreenPresenter(PhotoAttachment photo) {
      this.photoAttachment = photo;
   }

   @Override
   public void onInjected() {
      super.onInjected();
      timestampFormatter = new ChatTimestampFormatter(context.getApplicationContext());
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      flaggingPresenter = view.getFlaggingView().getPresenter();
      view.setContent(photoAttachment);
      view.setShowFlag(photoAttachment.isFlaggingEnabled());
      messageDAO.getMessage(photoAttachment.getMessageId()).take(1).subscribe(message -> {
         int messageStatus = message.getStatus();
         if (messageStatus == MessageStatus.ERROR || messageStatus == MessageStatus.SENDING) {
            return;
         }
         if (photoAttachment.getDate() != null) {
            String dateLabel = timestampFormatter.getMessageTimestamp(photoAttachment.getDate().getTime());
            view.setDateLabel(dateLabel);
         }
      });
   }

   public void onFlagPressed() {
      flaggingPresenter.flagMessage(photoAttachment.getConversationId(), photoAttachment.getMessageId());
   }

   public void onShareAction() {
      if (!isConnected()) {
         reportNoConnectionWithOfflineErrorPipe(new IOException());
         return;
      }

      view.onShowShareOptions();
   }

   public void onShareOptionChosen(@ShareType String type) {
      if (!isConnected()) {
         reportNoConnection();
         return;
      }
      if (type.equals(ShareType.EXTERNAL_STORAGE)) {
         tripImagesInteractor.getDownloadImageActionPipe()
               .createObservable(new DownloadImageCommand(photoAttachment.getUrl()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                     .onFail(this::handleError));
      } else {
         view.openShare(photoAttachment.getUrl(), "", type);
      }
   }

   public interface View extends Presenter.View {
      void setContent(PhotoAttachment photoAttachment);

      void onShowShareOptions();

      void openShare(String imageUrl, String text, @ShareType String type);

      void setDateLabel(String dateLabel);

      FlaggingView getFlaggingView();

      void setShowFlag(boolean showFlag);
   }

}
