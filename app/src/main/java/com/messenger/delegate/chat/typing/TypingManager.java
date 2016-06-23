package com.messenger.delegate.chat.typing;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;

import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

import rx.Observable;

public class TypingManager {
    private final TypingStore typingStore;
    private final UsersDAO usersDAO;

    public TypingManager(MessengerServerFacade serverFacade, UsersDAO usersDAO, TypingStore typingStore) {
        this.typingStore = typingStore;
        this.usersDAO = usersDAO;

        serverFacade.getStatusObservable()
                .filter(this::filterConnectionStatus)
                .subscribe(connectionStatus -> clearCache());
    }

    private boolean filterConnectionStatus(ConnectionStatus connectionStatus) {
        return connectionStatus == ConnectionStatus.DISCONNECTED || connectionStatus == ConnectionStatus.ERROR;
    }

    private void clearCache() {
        typingStore.deleteAll();
    }

    public void userStartTyping(String conversationId, String userId) {
        typingStore.add(conversationId, userId);
    }

    public void userStopTyping(String conversationId, String userId) {
        typingStore.delete(conversationId, userId);
    }

    public void userOffline(String userId) {
        typingStore.deleteByUserId(userId);
    }

    public Observable<List<DataUser>> getTypingObservable(String conversationId) {
        return typingStore.getTypingUsers(conversationId)
                .map(this::obtainDataUser)
                .doOnNext(Collections::sort);
    }

    private List<DataUser> obtainDataUser(List<String> userIds) {
        return usersDAO.getExitingUserByIds(userIds).toBlocking().first();
    }

    @Value.Immutable
    interface TypingModel {

        String getUserId();

        String getConversationId();
    }
}
