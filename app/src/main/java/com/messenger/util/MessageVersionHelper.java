package com.messenger.util;

import com.messenger.entities.DataMessage;
import com.messenger.messengerservers.constant.AttachmentType;

public class MessageVersionHelper {

    public static boolean isUnsupported(int version, String attachmentType) {
        return DataMessage.MESSAGE_FORMAT_VERSION < version ||
                AttachmentType.UNSUPPORTED.equals(attachmentType);
    }
}
