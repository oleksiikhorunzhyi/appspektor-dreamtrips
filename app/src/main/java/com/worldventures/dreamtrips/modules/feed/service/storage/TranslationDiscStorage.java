package com.worldventures.dreamtrips.modules.feed.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateTextCachedCommand;

public class TranslationDiscStorage implements ActionStorage<String> {

   private final SnappyRepository snappyRepository;

   public TranslationDiscStorage(SnappyRepository snappyRepository) {
      this.snappyRepository = snappyRepository;
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return TranslateTextCachedCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, String data) {
      checkBundle(params);
      //
      String originalText = params.get(TranslateTextCachedCommand.ORIGINAL_TEXT);
      String languageTo = params.get(TranslateTextCachedCommand.LANGUAGE_TO);
      //
      snappyRepository.saveTranslation(originalText, data, languageTo);
   }

   @Override
   public String get(@Nullable CacheBundle params) {
      checkBundle(params);
      //
      String originalText = params.get(TranslateTextCachedCommand.ORIGINAL_TEXT);
      String languageTo = params.get(TranslateTextCachedCommand.LANGUAGE_TO);

      return snappyRepository.getTranslation(originalText, languageTo);
   }

   private void checkBundle(@Nullable CacheBundle params) {
      if (params == null) {
         throw new AssertionError("params are null");
      }
      if (!params.contains(TranslateTextCachedCommand.ORIGINAL_TEXT)) {
         throw new AssertionError("Original text was not found");
      }
      if (!params.contains(TranslateTextCachedCommand.LANGUAGE_TO)) {
         throw new AssertionError("Language To was not found");
      }
   }

}
