package com.messenger.util;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class OpenedConversationTracker {

    private Map<String, Integer> openedConversationIds;

    public OpenedConversationTracker() {
        //expected max opened 2 conversation at he same time from push notifications
        openedConversationIds = new HashMap<>(2);
    }

    public void addOpenedConversation(String conversationId) {
        Timber.i("add conversation %s", conversationId);
        Integer prevCount = openedConversationIds.get(conversationId);
        if (prevCount == null) prevCount = 0;
        openedConversationIds.put(conversationId, prevCount + 1);
    }

    public void removeOpenedConversation(String conversationId) {
        Timber.i("remove conversation %s", conversationId);
        Integer prevCount = openedConversationIds.get(conversationId);
        if (prevCount == null || prevCount < 0) {
            openedConversationIds.remove(conversationId);
            return;
        }
        openedConversationIds.put(conversationId, prevCount - 1);
    }

    public boolean containsOpenedConversationId(String conversationId) {
        Integer prevCount = openedConversationIds.get(conversationId);
        return prevCount != null && prevCount > 0;
    }
}
