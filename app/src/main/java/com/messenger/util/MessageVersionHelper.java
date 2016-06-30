package com.messenger.util;

import com.messenger.messengerservers.constant.AttachmentType;

public class MessageVersionHelper {

    public static boolean isUnsupported(String attachmentType) {
        return AttachmentType.UNSUPPORTED.equals(attachmentType);
    }
}
