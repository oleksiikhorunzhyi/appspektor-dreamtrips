package com.messenger.util;

import com.messenger.messengerservers.constant.AttachmentType;

public final class MessageVersionHelper {

   private MessageVersionHelper() {
   }

   public static boolean isUnsupported(String attachmentType) {
      return AttachmentType.UNSUPPORTED.equals(attachmentType);
   }
}
