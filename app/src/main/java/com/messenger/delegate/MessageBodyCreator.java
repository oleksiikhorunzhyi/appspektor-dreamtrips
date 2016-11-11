package com.messenger.delegate;

import android.text.TextUtils;

import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.MessageBody;

import java.util.Collections;
import java.util.Locale;

public class MessageBodyCreator {

   public MessageBody provideForAttachment(AttachmentHolder attachmentHolder) {
      return provideForTextAndAttachment(null, attachmentHolder);
   }

   public MessageBody provideForText(String text) {
      return provideForTextAndAttachment(text, null);
   }

   public MessageBody provideForTextAndAttachment(String text, AttachmentHolder attachmentHolder) {
      MessageBody.Builder builder = new MessageBody.Builder();

      if (attachmentHolder != null) builder.attachments(Collections.singletonList(attachmentHolder));

      if (!TextUtils.isEmpty(text)) builder.text(text);

      return builder.locale(generateMessageLocale(Locale.getDefault())).build();
   }

   private String generateMessageLocale(Locale locale) {
      return String.format("%s-%s", locale.getLanguage(), locale.getCountry()).toLowerCase();
   }
}
