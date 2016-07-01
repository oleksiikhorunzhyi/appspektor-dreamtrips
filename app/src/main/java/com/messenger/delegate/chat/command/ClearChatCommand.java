package com.messenger.delegate.chat.command;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.ChatExtensions;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class ClearChatCommand extends Command<Void> implements InjectableAction {

    @Inject ChatExtensions chatExtensions;
    @Inject ConversationsDAO conversationsDAO;
    @Inject MessageDAO messageDAO;
    @Inject PhotoDAO photoDAO;

    private final String conversationId;

    public ClearChatCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        chatExtensions
                .clearChat(conversationId, System.currentTimeMillis())
                .doOnNext(event -> conversationsDAO.setClearDate(event.getConversationId(), event.clearTime()))
                .flatMap(event -> clearCache(event.getConversationId()))
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<Void> clearCache(String conversationId) {
        return clearImageCache(conversationId)
                .doOnNext(aVoid -> {
                    messageDAO.deleteMessagesByConversation(conversationId);
                    conversationsDAO.setUnreadCount(conversationId, 0);
                });
    }

    private Observable<Void> clearImageCache(String conversationId) {
        return photoDAO.getPhotoAttachments(conversationId)
                .take(1)
                .doOnNext(this::clearFrescoCache)
                .map(photos -> null);
    }

    private void clearFrescoCache(List<DataPhotoAttachment> photoAttachments) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        for (DataPhotoAttachment photo : photoAttachments) {
            String url = photo.getUrl();
            if (TextUtils.isEmpty(url)) continue;
            imagePipeline.evictFromCache(Uri.parse(url));
        }

    }
}
