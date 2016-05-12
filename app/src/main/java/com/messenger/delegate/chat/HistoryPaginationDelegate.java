package com.messenger.delegate.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.delegate.UsersDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

import static com.innahema.collections.query.queriables.Queryable.from;

public class HistoryPaginationDelegate {
    private final DecomposeMessagesHelper decomposeMessagesHelper;
    private final UsersDelegate usersDelegate;

    private final PagePagination<Message> messagePagePagination;
    private final Observable<List<Message>> pageObservable;
    private final MessageDAO messageDAO;

    @Inject
    HistoryPaginationDelegate(MessengerServerFacade messengerServerFacade,
                              DecomposeMessagesHelper decomposeMessagesHelper,
                              UsersDelegate usersDelegate, MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
        this.messagePagePagination = messengerServerFacade.getPaginationManager()
                .getConversationHistoryPagination();
        this.decomposeMessagesHelper = decomposeMessagesHelper;
        this.usersDelegate = usersDelegate;

        pageObservable = messagePagePagination.getPageObservable()
                .compose(this::filterAndRemoveDeletedMessages)
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
        List<String> usersIds = from(messages).map(Message::getFromId).toList();
        if (!usersIds.isEmpty()) return usersDelegate.loadIfNeedUsers(usersIds).map(users -> messages);
        return Observable.just(messages);
    }

    public Observable<List<Message>> getPageObservable() {
        return pageObservable;
    }

    private Observable<List<Message>> filterAndRemoveDeletedMessages(Observable<List<Message>> messagesObservable) {
        return messagesObservable
                .map(this::separateNonDeletedMessageAndRemoveDeleted);
    }

    private List<Message> separateNonDeletedMessageAndRemoveDeleted(@NonNull List<Message> messages) {
        if (messages.isEmpty()) return messages;

        Map<Boolean, Collection<Message>> messageGroup = from(messages).groupToMap(message -> message.getDeleted() != null);
        removeDeletedMessages(messageGroup.get(Boolean.TRUE));
        Collection<Message> nonDeletedMessages = messageGroup.get(Boolean.FALSE);
        return nonDeletedMessages == null ? Collections.emptyList() : new ArrayList<>(nonDeletedMessages);
    }

    private void removeDeletedMessages(@Nullable Collection<Message> messages) {
        if (messages == null || messages.isEmpty()) return;
        messageDAO.deleteById(from(messages).map(Message::getId).toList());
    }
}
