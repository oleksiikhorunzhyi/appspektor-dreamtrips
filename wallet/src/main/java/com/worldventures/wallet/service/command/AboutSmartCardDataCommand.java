package com.worldventures.wallet.service.command;

import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.wallet.domain.entity.AboutSmartCardData;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class AboutSmartCardDataCommand extends Command<AboutSmartCardData> implements CachedAction<AboutSmartCardData> {

   private final Func1<AboutSmartCardData, AboutSmartCardData> func;
   public final boolean update;
   private AboutSmartCardData cachedAboutSmartCardData;

   private AboutSmartCardDataCommand(boolean update, Func1<AboutSmartCardData, AboutSmartCardData> func) {
      this.update = update;
      this.func = func;
   }

   public static AboutSmartCardDataCommand fetch() {
      return new AboutSmartCardDataCommand(false, aboutSmartCardData -> aboutSmartCardData);
   }

   public static AboutSmartCardDataCommand save(AboutSmartCardData aboutSmartCardData) {
      return new AboutSmartCardDataCommand(true, user -> aboutSmartCardData);
   }

   @Override
   public AboutSmartCardData getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, AboutSmartCardData cache) {
      this.cachedAboutSmartCardData = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return new CacheOptions(!update, update, true, null);
   }

   @Override
   protected void run(CommandCallback<AboutSmartCardData> callback) throws Throwable {
      callback.onSuccess(func.call(cachedAboutSmartCardData));
   }
}
