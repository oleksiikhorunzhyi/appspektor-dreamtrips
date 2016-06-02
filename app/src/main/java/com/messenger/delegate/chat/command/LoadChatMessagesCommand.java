package com.messenger.delegate.chat.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.delegate.command.BaseChatAction;
import com.messenger.delegate.user.UsersDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.util.DecomposeMessagesHelper;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.innahema.collections.query.queriables.Queryable.from;

@CommandAction
public class LoadChatMessagesCommand extends BaseChatAction<List<Message>>
        implements InjectableAction {

    @Inject UsersDelegate usersDelegate;
    @Inject MessageDAO messageDAO;
    @Inject MessengerServerFacade messengerServerFacade;
    @Inject DecomposeMessagesHelper decomposeMessagesHelper;

    private int page;
    private int pageSize;
    private long beforeMessageTimestamp;

    public LoadChatMessagesCommand(String conversationId, int page,
                                   int pageSize, long beforeMessageTimestamp) {
        super(conversationId);
        this.page = page;
        this.pageSize = pageSize;
        this.beforeMessageTimestamp = beforeMessageTimestamp;
    }

    @Override
    protected void run(CommandCallback<List<Message>> callback) throws Throwable {
        PagePagination<Message> pagination = messengerServerFacade.getPaginationManager()
                .getConversationHistoryPagination();
        pagination.setPageSize(pageSize);
        pagination.loadPage(conversationId, page, beforeMessageTimestamp)
                .compose(this::filterAndRemoveDeletedMessages)
                .flatMap(this::prepareUsers)
                .doOnNext(this::saveMessages)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private void saveMessages(List<Message> messages) {
        DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(messages);
        from(result.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));
        decomposeMessagesHelper.saveDecomposeMessage(result);
    }

    private Observable<List<Message>> prepareUsers(List<Message> messages) {
        List<String> usersIds = from(messages).map(Message::getFromId).distinct().toList();
        if (!usersIds.isEmpty()) return usersDelegate.loadMissingUsers(usersIds).map(users -> messages);
        return Observable.just(messages);
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
        messageDAO.deleteMessageByIds(from(messages).map(Message::getId).toList());
    }
}
