package com.messenger.ui.helper;

import static com.messenger.messengerservers.constant.MessageType.MESSAGE;

public class MessageHelper {

    private MessageHelper() {
    }

    public static boolean isSystemMessage(String messageType) {
        return !MESSAGE.equals(messageType);
    }

    /**
     * @return true if type1 and type2 are different base types of messages.
     * One of them should user message, another one should be one of the system messages types.
     * Or vice versa.
     */
    public static boolean areDifferentUserOrSystemMessageTypes(String type1, String type2) {
        return isSystemMessage(type1) != isSystemMessage(type2);
    }
}
