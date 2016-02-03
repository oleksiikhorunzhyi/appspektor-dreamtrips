package com.messenger.delegate;

import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.MessageDAO;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class PaginationDelegate {
    private final MessengerServerFacade messengerServerFacade;
    private final MessageDAO messageDAO;
    private final int pageSize;

    private PagePagination<Message> messagePagePagination;

    public interface PageLoadedListener {
        void onPageLoaded(int loadedPage, List<Message> loadedMessage);
    }

    public interface PageErrorListener {
        void onPageError();
    }

    public PaginationDelegate(MessengerServerFacade messengerServerFacade, MessageDAO messageDAO, int pageSize) {
        this.messengerServerFacade = messengerServerFacade;
        this.messageDAO = messageDAO;
        this.pageSize = pageSize;
    }

    public void loadConversationHistoryPage(DataConversation conversation, int page, long before,
                                            @Nullable PageLoadedListener loadedListener, @Nullable PageErrorListener errorListener) {
        if (messagePagePagination == null) {
            messagePagePagination = messengerServerFacade.getPaginationManager()
                    .getConversationHistoryPagination(conversation.getId(), pageSize);
        }

        messagePagePagination.setPersister(
                messages -> Observable.just(messages)
                .subscribeOn(Schedulers.io())
                .map(serverMessages -> Queryable.from(serverMessages).map(DataMessage::new).toList())
                .subscribe(messageDAO::save))
        ;
        messagePagePagination.setOnEntityLoadedListener(new OnLoadedListener<Message>() {
            @Override
            public void onLoaded(List<Message> entities) {
                if (loadedListener == null) return;
                loadedListener.onPageLoaded(page, entities);
            }

            @Override
            public void onError(Exception e) {
                if (errorListener == null) return;
                errorListener.onPageError();
            }
        });
        messagePagePagination.loadPage(page, before);
    }

    public void stopPaginate(){
        if (messagePagePagination != null){
            messagePagePagination.close();
        }
        messagePagePagination = null;
    }

}
