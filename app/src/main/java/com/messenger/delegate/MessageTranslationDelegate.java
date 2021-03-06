package com.messenger.delegate;

import com.messenger.analytics.TranslateMessageAction;
import com.messenger.api.TranslationInteractor;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataTranslation;
import com.messenger.messengerservers.constant.TranslationStatus;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.util.SessionHolderHelper;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.api.messenger.TranslateTextHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.ImmutableTranslateTextBody;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

@Singleton
public class MessageTranslationDelegate {

   private final TranslationsDAO translationsDAO;
   private final SessionHolder sessionHolder;
   private final TranslationInteractor translationInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   @Inject
   public MessageTranslationDelegate(TranslationInteractor translationInteractor, TranslationsDAO translationsDAO,
         SessionHolder sessionHolder, AnalyticsInteractor analyticsInteractor) {
      this.translationInteractor = translationInteractor;
      this.translationsDAO = translationsDAO;
      this.sessionHolder = sessionHolder;
      this.analyticsInteractor = analyticsInteractor;
   }

   public void translateMessage(DataMessage message) {
      translationsDAO.getTranslation(message.getId()).first().subscribe(dataTranslation -> {
         String translateToLocale = LocaleHelper.getDefaultLocaleFormatted();
         if ((dataTranslation == null || dataTranslation.getTranslateStatus() == TranslationStatus.ERROR) && SessionHolderHelper
               .hasEntity(sessionHolder)) {
            translateMessageRequest(message, translateToLocale);
            return;
         }
         if (dataTranslation != null && dataTranslation.getTranslateStatus() == TranslationStatus.REVERTED) {
            analyticsInteractor.analyticsActionPipe().send(new TranslateMessageAction(translateToLocale));
            dataTranslation.setTranslateStatus(TranslationStatus.TRANSLATED);
            translationsDAO.save(dataTranslation);
         }
      });
   }

   private void translateMessageRequest(DataMessage dataMessage, String toLocale) {
      DataTranslation dataTranslation = new DataTranslation(dataMessage.getId(), null, TranslationStatus.TRANSLATING);
      translationsDAO.save(dataTranslation);

      translationInteractor.translatePipe()
            .createObservable(new TranslateTextHttpAction(ImmutableTranslateTextBody.builder()
                  .text(dataMessage.getText())
                  .toLanguage(toLocale)
                  .build()))
            .subscribe(new ActionStateSubscriber<TranslateTextHttpAction>().onSuccess(translateTextAction -> {
               analyticsInteractor.analyticsActionPipe().send(new TranslateMessageAction(toLocale));
               onTranslatedText(dataTranslation, translateTextAction.getTranslatedText());
            }).onFail((translateTextAction, throwable) -> onError(dataTranslation, throwable)));
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

   public void revertTranslation(DataMessage message) {
      translationsDAO.getTranslation(message.getId()).first().compose(new NonNullFilter<>()).map(dataTranslation -> {
         dataTranslation.setTranslateStatus(TranslationStatus.REVERTED);
         return dataTranslation;
      }).subscribe(translationsDAO::save);
   }
}
