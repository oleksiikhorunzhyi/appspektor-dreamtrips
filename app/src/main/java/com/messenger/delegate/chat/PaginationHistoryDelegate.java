package com.messenger.delegate.chat;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.UsersDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.util.DecomposeMessagesHelper;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

import static com.innahema.collections.query.queriables.Queryable.from;

public class PaginationHistoryDelegate {
    private final DecomposeMessagesHelper decomposeMessagesHelper;
    private final UsersDelegate usersDelegate;

    private final PagePagination<Message> messagePagePagination;
    private final Observable<List<Message>> pageObservable;

    @Inject
    PaginationHistoryDelegate(MessengerServerFacade messengerServerFacade, DecomposeMessagesHelper decomposeMessagesHelper, UsersDelegate usersDelegate) {
        this.messagePagePagination = messengerServerFacade.getPaginationManager()
                .getConversationHistoryPagination();
        this.decomposeMessagesHelper = decomposeMessagesHelper;
        this.usersDelegate = usersDelegate;

        pageObservable = messagePagePagination.getPageObservable()
                .compose(this::filterMessageList)
                .flatMap(this::prepareUsers)
                .doOnNext(this::saveMessages);
    }

    public void setPageSize(int pageSize) {
        messagePagePagination.setPageSize(pageSize);
    }

    public void loadConversationHistoryPage(DataConversation conversation, int page, long beforeTimestamp) {
        messagePagePagination.loadPage(conversation.getId(), page, beforeTimestamp);
    }

    private void saveMessages(List<Message> messages) {
        DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(messages);
        from(result.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
        decomposeMessagesHelper.saveDecomposeMessage(result);
    }

    private Observable<List<Message>> prepareUsers(List<Message> messages) {
        List<String> usersIds = Queryable.from(messages).map(Message::getFromId).toList();
        if (!usersIds.isEmpty()) return usersDelegate.loadIfNeedUsers(usersIds).map(users -> messages);
        return Observable.just(messages);
    }

    public Observable<List<Message>> getPageObservable() {
        return pageObservable;
    }

    private Observable<List<Message>> filterMessageList(Observable<List<Message>> messagesObservable) {
        return messagesObservable
                .flatMap(Observable::from)
                .filter(message -> message.getDeleted() == null)
                .toList();
    }
}
