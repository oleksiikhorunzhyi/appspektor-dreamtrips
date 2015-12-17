package com.messenger.delegate;

import android.content.Context;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

public class PaginationDelegate {

    final Context context;
    final MessengerServerFacade messengerServerFacade;
    final int pageSize;

    PagePagination<Message> messagePagePagination;

    public interface PageLoadedListener{
        void onPageLoaded(int loadedPage, boolean haveMoreElement);
    }

    public PaginationDelegate(Context context, MessengerServerFacade messengerServerFacade, int pageSize) {
        this.context = context;
        this.messengerServerFacade = messengerServerFacade;
        this.pageSize = pageSize;
    }

    public void loadConversationHistoryPage (Conversation conversation, int page, @Nullable PageLoadedListener listener) {
        if (messagePagePagination == null) {
            messagePagePagination = messengerServerFacade.getPaginationManager()
                    .getConversationHistoryPagination(conversation, pageSize);
        }

        messagePagePagination.setPersister(messages -> ContentUtils.bulkInsert(Message.CONTENT_URI, Message.class, messages));
        messagePagePagination.setOnEntityLoadedListener(messages -> {
            if (listener == null) return;
            listener.onPageLoaded(page, pageSize == messages.size());
        });
        messagePagePagination.loadPage(page);
    }

}
