package com.worldventures.wallet.service.command;

import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;
import com.worldventures.wallet.domain.entity.SmartCardUser;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public final class SmartCardUserCommand extends Command<SmartCardUser> implements CachedAction<SmartCardUser> {

   private final Func1<SmartCardUser, SmartCardUser> func;
   private final boolean save;

   private SmartCardUser cachedUser;

   private SmartCardUserCommand(boolean save, Func1<SmartCardUser, SmartCardUser> func) {
      this.func = func;
      this.save = save;
   }

   public static SmartCardUserCommand fetch() {
      return new SmartCardUserCommand(false, smartCardUser -> smartCardUser);
   }

   public static SmartCardUserCommand save(SmartCardUser smartCardUser) {
      return new SmartCardUserCommand(true, user -> smartCardUser);
   }

   public static SmartCardUserCommand update(Func1<SmartCardUser, SmartCardUser> func) {
      return new SmartCardUserCommand(true, func);
   }

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      callback.onSuccess(func.call(cachedUser));
   }

   @Override
   public SmartCardUser getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCardUser cache) {
      this.cachedUser = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return new CacheOptions(true, save, true, null);
   }
}
