package com.messenger.util;

import android.text.TextUtils;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.Collection;
import java.util.List;

import static com.innahema.collections.query.queriables.Queryable.from;

public class TranslationStatusHelper {

    private final LocaleHelper localeHelper;

    public TranslationStatusHelper(LocaleHelper localeHelper) {
        this.localeHelper = localeHelper;
    }

    public List<DataTranslation> obtainNativeTranslations(Collection<DataMessage> messages, SessionHolder<UserSession> userSessionHolder) {
        User user = userSessionHolder.get().get().getUser();
        String userLanguage = localeHelper.obtainLanguageCode(user.getLocale());
        String userId = user.getUsername();

        List<DataTranslation> translations = from(messages).filter(msg -> !TextUtils.equals(msg.getFromId(), userId))
                .filter(msg -> TextUtils.equals(userLanguage, localeHelper.obtainLanguageCode(msg.getLocaleName())))
                .map(msg -> new DataTranslation(msg.getId(), null, TranslationStatus.NATIVE)).toList();

        translations.addAll(from(messages).filter(msg -> TextUtils.equals(msg.getFromId(), userId))
                .map(msg -> new DataTranslation(msg.getId(), null, TranslationStatus.NATIVE)).toList());

        return translations;
    }

}
