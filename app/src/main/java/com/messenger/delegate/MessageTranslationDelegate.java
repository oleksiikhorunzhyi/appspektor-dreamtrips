package com.messenger.delegate;

import com.messenger.api.TranslateTextAction;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.util.SessionHolderHelper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.Janet;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class MessageTranslationDelegate {

    private Janet janet;
    private TranslationsDAO translationsDAO;
    private LocaleHelper localeHelper;
    private SessionHolder<UserSession> sessionHolder;

    @Inject
    public MessageTranslationDelegate(Janet janet, TranslationsDAO translationsDAO,
                                      LocaleHelper localeHelper, SessionHolder<UserSession> sessionHolder) {
        this.janet = janet;
        this.translationsDAO = translationsDAO;
        this.localeHelper = localeHelper;
        this.sessionHolder = sessionHolder;
    }

    public void translateMessage(DataMessage message) {
        translationsDAO.getTranslation(message.getId()).first()
                .subscribe(dataTranslation -> {
                    String translateToLocale = localeHelper.getAccountLocaleFormatted(sessionHolder.get().get().getUser());
                    if ((dataTranslation == null || dataTranslation.getTranslateStatus() == TranslationStatus.ERROR)
                            && SessionHolderHelper.hasEntity(sessionHolder)){
                        translateMessageRequest(message, translateToLocale);
                        return;
                    }
                    if (dataTranslation != null && dataTranslation.getTranslateStatus() == TranslationStatus.REVERTED){
                        TrackingHelper.translateMessage(translateToLocale);
                        dataTranslation.setTranslateStatus(TranslationStatus.TRANSLATED);
                        translationsDAO.save(dataTranslation);
                    }
                });
    }

    private void translateMessageRequest(DataMessage dataMessage, String toLocale) {
        DataTranslation dataTranslation = new DataTranslation(dataMessage.getId(), null, TranslationStatus.TRANSLATING);
        translationsDAO.save(dataTranslation);

        janet.createPipe(TranslateTextAction.class, Schedulers.io())
                .createObservable(new TranslateTextAction(dataMessage.getText(), toLocale))
                .subscribe(new ActionStateSubscriber<TranslateTextAction>()
                        .onSuccess(translateTextAction -> {
                            TrackingHelper.translateMessage(toLocale);
                            onTranslatedText(dataTranslation, translateTextAction.getTranslatedText());
                        })
                        .onFail((translateTextAction, throwable) -> onError(dataTranslation, throwable)));
    }

    private void onTranslatedText(DataTranslation dataTranslation, String translatedText) {
        dataTranslation.setTranslation(translatedText);
        dataTranslation.setTranslateStatus(TranslationStatus.TRANSLATED);
        translationsDAO.save(dataTranslation);
    }

    private void onError(DataTranslation dataTranslation, Throwable throwable) {
        Timber.e(throwable, "Exception while translation message");
        dataTranslation.setTranslateStatus(TranslationStatus.ERROR);
        translationsDAO.save(dataTranslation);
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