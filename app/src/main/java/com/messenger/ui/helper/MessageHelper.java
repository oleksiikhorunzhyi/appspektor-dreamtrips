package com.messenger.ui.helper;

import static com.messenger.messengerservers.constant.MessageType.MESSAGE;

public class MessageHelper {

    private MessageHelper() {
    }

    public static boolean isSystemMessage(String messageType) {
        return !MESSAGE.equals(messageType);
    }
}
