package com.worldventures.dreamtrips.wallet.service.command.device;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class SmartCardFirmwareCommand extends Command<SmartCardFirmware> implements CachedAction<SmartCardFirmware> {

   private final Func1<SmartCardFirmware, SmartCardFirmware> func;
   private SmartCardFirmware cachedSmartCardFirmware;

   private SmartCardFirmwareCommand(Func1<SmartCardFirmware, SmartCardFirmware> func) {
      this.func = func;
   }

   public static SmartCardFirmwareCommand fetch() {
      return new SmartCardFirmwareCommand(smartCardFirmware -> smartCardFirmware);
   }

   public static SmartCardFirmwareCommand bundleVersion(String bundleVersion) {
      return update(builder -> builder.firmwareBundleVersion(bundleVersion));
   }

   public static SmartCardFirmwareCommand save(SmartCardFirmware smartCardFirmware) {
      return new SmartCardFirmwareCommand(smartCardFirmware1 -> smartCardFirmware);
   }

   @Override
   protected void run(CommandCallback<SmartCardFirmware> callback) throws Throwable {
      if (cachedSmartCardFirmware == null) cachedSmartCardFirmware = createDefault();
      SmartCardFirmware smartCardFirmware = func.call(cachedSmartCardFirmware);
      callback.onSuccess(smartCardFirmware);
   }

   @Override
   public SmartCardFirmware getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCardFirmware cache) {
      cachedSmartCardFirmware = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(true)
            .restoreFromCache(true)
            .sendAfterRestore(true)
            .build();
   }

   private SmartCardFirmware createDefault() {
      return ImmutableSmartCardFirmware.builder().build();
   }

   private static SmartCardFirmwareCommand update(Func1<ImmutableSmartCardFirmware.Builder, ImmutableSmartCardFirmware.Builder> builderFunc) {
      return new SmartCardFirmwareCommand(smartCardFirmware ->
            builderFunc.call(ImmutableSmartCardFirmware.builder().from(smartCardFirmware)).build()
      );
   }
}
