package com.worldventures.dreamtrips.wallet.service.command.device;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class DeviceStateCommand extends Command<SmartCardStatus> implements CachedAction<SmartCardStatus> {

   private final Func1<SmartCardStatus, SmartCardStatus> func;
   private SmartCardStatus cachedSmartCardStatus;

   private DeviceStateCommand(Func1<SmartCardStatus, SmartCardStatus> func) {
      this.func = func;
   }

   public static DeviceStateCommand fetch() {
      return new DeviceStateCommand(smartCardStatus -> smartCardStatus);
   }

   public static DeviceStateCommand lock(boolean lock) {
      return update(builder -> builder.lock(lock));
   }

   public static DeviceStateCommand stealthMode(boolean stealthMode) {
      return update(builder -> builder.stealthMode(stealthMode));
   }

   public static DeviceStateCommand connection(ConnectionStatus connectionStatus) {
      return update(builder -> builder.connectionStatus(connectionStatus));
   }

   public static DeviceStateCommand battery(int batteryLevel) {
      return update(builder -> builder.batteryLevel(batteryLevel));
   }

   @Override
   protected void run(CommandCallback<SmartCardStatus> callback) throws Throwable {
      if (cachedSmartCardStatus == null) cachedSmartCardStatus = createDefault();
      SmartCardStatus newSmartCardStatus = func.call(cachedSmartCardStatus);
      callback.onSuccess(newSmartCardStatus);
   }

   @Override
   public SmartCardStatus getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, SmartCardStatus cache) {
      cachedSmartCardStatus = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(true)
            .restoreFromCache(true)
            .sendAfterRestore(true)
            .build();
   }

   private SmartCardStatus createDefault() {
      return ImmutableSmartCardStatus.builder().build();
   }

   private static DeviceStateCommand update(Func1<ImmutableSmartCardStatus.Builder, ImmutableSmartCardStatus.Builder> builderFunc) {
      return new DeviceStateCommand(smartCardStatus ->
            builderFunc.call(ImmutableSmartCardStatus.builder().from(smartCardStatus)).build()
      );
   }
}
