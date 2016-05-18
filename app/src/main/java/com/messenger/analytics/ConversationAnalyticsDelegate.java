package com.messenger.analytics;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.ui.helper.ConversationHelper;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ConversationAnalyticsDelegate {

    public static final String MESSENGER_ACTION_VIEW_CONVERSATION = "Messenger:View Conversation";

    public static final String MESSENGER_VALUE_INDIVIDUAL = "Individual";
    public static final String MESSENGER_VALUE_IN_DESTINATION_INDIVIDUAL = "InDestination-Individual";
    public static final String MESSENGER_VALUE_GROUP = "Group-%d";
    public static final String MESSENGER_VALUE_TRIP_CHAT = "DreamTrip-%d";
    public static final String MESSENGER_VALUE_TRIP_CHAT_WITH_HOST = "DreamTrip-InDestination-Group-%d";

    public static final String MESSENGER_ATTRIBUTE_CONVERSATION_TYPE = "convotype";
    public static final String MESSENGER_ATTRIBUTE_GROUP_CHAT_NAME = "groupchatname";

    @Inject
    public ConversationAnalyticsDelegate() {
    }

    public void trackOpenedConversation(@NotNull DataConversation openedConversation,
                                        @NotNull List<DataUser> participants) {
        AnalyticsType analyticsType = obtainAnalyticsType(openedConversation, participants);
        String conversationSubject = ConversationHelper.obtainConversationSubject(openedConversation, participants);
        int count = participants.size();
        //
        switch (analyticsType) {
            case CHAT:
                openSingleConversation(MESSENGER_VALUE_INDIVIDUAL);
                break;
            case CHAT_WITH_HOST:
                openSingleConversation(MESSENGER_VALUE_IN_DESTINATION_INDIVIDUAL);
                break;
            case GROUP_CHAT:
                openGroupConversation(MESSENGER_VALUE_GROUP,
                        conversationSubject, count);
                break;
            case TRIP_CHAT:
                openGroupConversation(MESSENGER_VALUE_TRIP_CHAT,
                        conversationSubject, count);
                break;
            case TRIP_CHAT_WITH_HOST:
                openGroupConversation(MESSENGER_VALUE_TRIP_CHAT_WITH_HOST,
                        conversationSubject, count);
                break;
        }
    }

    private void openSingleConversation(String actionName) {
        Map<String, Object> data = new HashMap<>();
        data.put(MESSENGER_ATTRIBUTE_CONVERSATION_TYPE, actionName);

        TrackingHelper.sendActionToAdobe(MESSENGER_ACTION_VIEW_CONVERSATION, data);
    }


    private void openGroupConversation(String conversationValue, String subject, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put(MESSENGER_ATTRIBUTE_CONVERSATION_TYPE, String.format(conversationValue, count));
        data.put(MESSENGER_ATTRIBUTE_GROUP_CHAT_NAME, subject);

        TrackingHelper.sendActionToAdobe(MESSENGER_ACTION_VIEW_CONVERSATION, data);
    }

    private AnalyticsType obtainAnalyticsType(DataConversation conversation, List<DataUser> participants) {
        boolean chatWithHost = Queryable.from(participants).firstOrDefault(DataUser::isHost) != null;

        if (TextUtils.equals(conversation.getType(), ConversationType.CHAT)) {
            return chatWithHost ? AnalyticsType.CHAT_WITH_HOST : AnalyticsType.CHAT;
        } else {
            return obtainGroupAnalyticsType(conversation, chatWithHost);
        }
    }

    private AnalyticsType obtainGroupAnalyticsType(DataConversation conversation, boolean chatWithHost) {
        boolean isTripChat = ConversationHelper.isTripChat(conversation);
        if (isTripChat) {
            return chatWithHost ? AnalyticsType.TRIP_CHAT_WITH_HOST :
                    AnalyticsType.TRIP_CHAT;
        } else return AnalyticsType.GROUP_CHAT;
    }
}
