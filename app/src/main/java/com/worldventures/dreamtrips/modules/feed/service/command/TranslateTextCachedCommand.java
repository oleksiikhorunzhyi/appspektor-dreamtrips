package com.worldventures.dreamtrips.modules.feed.service.command;

import com.messenger.api.TranslationInteractor;
import com.worldventures.dreamtrips.api.messenger.TranslateTextHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.ImmutableTranslateTextBody;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class TranslateTextCachedCommand extends Command<String> implements CachedAction<String>, InjectableAction {
   public static final String ORIGINAL_TEXT = "originalText";
   public static final String LANGUAGE_TO = "languageTo";

   @Inject TranslationInteractor translationInteractor;

   private String cachedTranslation;

   private String originalText;
   private String languageTo;

   public TranslateTextCachedCommand(String originalText, String languageTo) {
      this.originalText = originalText;
      this.languageTo = languageTo;
   }

   @Override
   protected void run(CommandCallback<String> callback) throws Throwable {
      if (cachedTranslation == null || cachedTranslation.length() == 0) translationInteractor.translatePipe()
            .createObservableResult(new TranslateTextHttpAction(ImmutableTranslateTextBody.builder()
                  .text(originalText)
                  .toLanguage(languageTo)
                  .build()))
            .map(TranslateTextHttpAction::getTranslatedText)
            .subscribe(callback::onSuccess, callback::onFail);
      else callback.onSuccess(cachedTranslation);
   }

   @Override
   public String getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, String cache) {
      cachedTranslation = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle bundle = new CacheBundleImpl();
      bundle.put(ORIGINAL_TEXT, originalText);
      bundle.put(LANGUAGE_TO, languageTo);

      return ImmutableCacheOptions.builder().params(bundle).build();
   }
}
