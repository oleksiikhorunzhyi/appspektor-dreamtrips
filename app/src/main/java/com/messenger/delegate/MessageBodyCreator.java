package com.messenger.delegate;

import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.MessageBody;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Collections;
import java.util.Locale;

public class MessageBodyCreator {

    private LocaleHelper localeHelper;
    private User currentUser;

    public MessageBodyCreator(LocaleHelper localeHelper, User currentUser) {
        this.localeHelper = localeHelper;
        this.currentUser = currentUser;
    }

    public MessageBody provideForAttachment(AttachmentHolder attachmentHolder) {
        return provideForTextAndAttachment(null, attachmentHolder);
    }

    public MessageBody provideForText(String text) {
        return provideForTextAndAttachment(text, null);
    }

    public MessageBody provideForTextAndAttachment(String text, AttachmentHolder attachmentHolder) {
        return new MessageBody.Builder()
                .text(text)
                .attachments(Collections.singletonList(attachmentHolder))
                .locale(generateMessageLocale(localeHelper.getAccountLocale(currentUser)))
                .build();
    }

    private String generateMessageLocale(Locale locale) {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry()).toLowerCase();
    }
}
