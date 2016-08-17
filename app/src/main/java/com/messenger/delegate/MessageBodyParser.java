package com.messenger.delegate;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.util.Utils;

import java.util.List;

import timber.log.Timber;

public class MessageBodyParser {

   private static final MessageBody UNSUPPORTED_MESSAGE_BODY = new MessageBody.Builder().text("This message is invalid")
         .build();

   private final Gson gson;

   public MessageBodyParser(Gson gson) {
      this.gson = gson;
   }

   public MessageBody parseMessageBody(String stanzaMessageBody) {
      String messageBodyJson = Utils.unescapeXML(stanzaMessageBody);

      MessageBody messageBody;

      try {
         messageBody = gson.fromJson(messageBodyJson, MessageBody.class);
         if (!validateMessageBody(messageBody)) messageBody = UNSUPPORTED_MESSAGE_BODY;
      } catch (JsonSyntaxException syntaxException) {
         Timber.e(syntaxException, "Fail to parse message body");
         messageBody = UNSUPPORTED_MESSAGE_BODY;
      }

      return messageBody;
   }

   private boolean validateMessageBody(MessageBody messageBody) {
      List<AttachmentHolder> attachments = messageBody.getAttachments();
      if (TextUtils.isEmpty(messageBody.getText()) && (attachments == null || attachments.isEmpty())) {
         return false;
      } else if (attachments != null && !attachments.isEmpty()) {
         for (AttachmentHolder attachment : attachments) {
            if (attachment == null) return false;
         }
      }
      return true;
   }
}
