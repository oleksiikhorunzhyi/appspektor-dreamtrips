package com.messenger.delegate;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.MessageDAO;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.innahema.collections.query.queriables.Queryable.from;

public class PaginationDelegate {

    public static final int DEFAULT_PAGE_SIZE = 20;

    private final MessengerServerFacade messengerServerFacade;
    private final MessageDAO messageDAO;
    private final AttachmentDAO attachmentDAO;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private PagePagination<Message> messagePagePagination;

    public PaginationDelegate(MessengerServerFacade messengerServerFacade, MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        this.messengerServerFacade = messengerServerFacade;
        this.messageDAO = messageDAO;
        this.attachmentDAO = attachmentDAO;
    }

    public void setPageSize(int pageSize) {
        stopPaginate();
        this.pageSize = pageSize;
    }

    public void loadConversationHistoryPage(DataConversation conversation, int page, long before,
                                            @Nullable PageLoadedListener loadedListener, @Nullable PageErrorListener errorListener) {
        if (messagePagePagination == null) {
            messagePagePagination = messengerServerFacade.getPaginationManager()
                    .getConversationHistoryPagination(conversation.getId(), pageSize);
        }

        messagePagePagination.setPersister(messages ->
                Observable.just(messages)
                .subscribeOn(Schedulers.io())
                .map(serverMessages -> {
                    List<DataMessage> msgs = from(serverMessages).map(DataMessage::new).toList();
                    List<DataAttachment> attachments = getDataAttachment(serverMessages);
                    from(msgs).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
                    return new Pair<>(msgs, attachments);
                }).subscribe(listListPair -> {
                            messageDAO.save(listListPair.first);
                            attachmentDAO.save(listListPair.second);
                        }));

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

    private List<DataAttachment> getDataAttachment(List<Message> messages) {
        List<DataAttachment> result = new LinkedList<>();
        for (Message m : messages) {
            result.addAll(DataAttachment.fromMessage(m));
        }
        return result;
    }

    public void stopPaginate(){
        if (messagePagePagination != null){
            messagePagePagination.close();
        }
        messagePagePagination = null;
    }

    public interface PageLoadedListener {
        void onPageLoaded(int loadedPage, List<Message> loadedMessage);
    }

    public interface PageErrorListener {
        void onPageError();
    }
}
