package com.messenger.delegate;

import android.content.Context;

import com.messenger.api.TranslateTextBody;
import com.messenger.api.TranslateTextQuery;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.util.SessionHolderHelper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import rx.Observable;
import timber.log.Timber;

public class MessageTranslationDelegate {

    private DreamSpiceManager dreamSpiceManager;
    private TranslationsDAO translationsDAO;
    private LocaleHelper localeHelper;

    public MessageTranslationDelegate(Context context, DreamSpiceManager dreamSpiceManager, TranslationsDAO translationsDAO, LocaleHelper localeHelper) {
        this.dreamSpiceManager = dreamSpiceManager;
        this.translationsDAO = translationsDAO;
        this.localeHelper = localeHelper;
        dreamSpiceManager.start(context);
    }

    public void translateMessage(DataMessage message, SessionHolder<UserSession> userSession) {
        translationsDAO.getTranslation(message.getId()).first()
                .subscribe(dataTranslation -> {
                    if ((dataTranslation == null || dataTranslation.getTranslateStatus() == TranslationStatus.ERROR)
                            && SessionHolderHelper.hasEntity(userSession)){
                        translateMessageRequest(message, localeHelper.
                                getAccountLocaleFormatted(userSession.get().get().getUser()));
                    }
                    if (dataTranslation.getTranslateStatus() == TranslationStatus.REVERTED){
                        dataTranslation.setTranslateStatus(TranslationStatus.TRANSLATED);
                        translationsDAO.save(dataTranslation);
                    }
                });
    }

    private void translateMessageRequest(DataMessage dataMessage, String toLocale) {
        TranslateTextBody body = new TranslateTextBody(dataMessage.getText(), toLocale);

        DataTranslation dataTranslation = new DataTranslation(dataMessage.getId(), null, TranslationStatus.TRANSLATING);
        translationsDAO.save(dataTranslation);

        Observable.<DataTranslation>create(subscriber -> {
            dreamSpiceManager.execute(new TranslateTextQuery(body), translatedText -> {
                if (!subscriber.isUnsubscribed()) {
                    dataTranslation.setTranslation(translatedText);
                    dataTranslation.setTranslateStatus(TranslationStatus.TRANSLATED);
                    subscriber.onNext(dataTranslation);
                    subscriber.onCompleted();
                }
            }, spiceException -> {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(spiceException);
                }
            });
        }).subscribe(translationsDAO::save, throwable -> {
            Timber.e(throwable, "Exception while translation message");
            dataTranslation.setTranslateStatus(TranslationStatus.ERROR);
            translationsDAO.save(dataTranslation);
        });
    }

    public void revertTranslation(DataMessage message){
        translationsDAO.getTranslation(message.getId()).first()
                .compose(new NonNullFilter<>())
                .map(dataTranslation -> {
                    dataTranslation.setTranslateStatus(TranslationStatus.REVERTED);
                    return dataTranslation;
                }).subscribe(translationsDAO::save);
    }
}
