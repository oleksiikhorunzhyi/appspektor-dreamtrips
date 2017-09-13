package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.jaredrummler.android.device.DeviceName;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.session.model.ImmutableDevice;
import com.worldventures.dreamtrips.core.utils.FilePathProvider;
import com.worldventures.dreamtrips.modules.background_uploading.util.FileSplitter;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.AppInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.ConnectionInfoProviderImpl;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProviderImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.schedulers.Schedulers;

@Module(library = true, complete = false)
public class DeviceModule {

   @Provides
   @Singleton
   Observable<DeviceName.DeviceInfo> provideDeviceInfo(@ForApplication Context context) {
      return Observable.defer(() -> Observable.just(DeviceName.getDeviceInfo(context))).subscribeOn(Schedulers.io());
   }

   @Provides
   @Singleton
   Observable<Device> provideDevice(Observable<DeviceName.DeviceInfo> source) {
      return source.map(deviceInfo ->
            ImmutableDevice.builder()
                  .manufacturer(deviceInfo.manufacturer)
                  .model(deviceInfo.marketName)
                  .build()
      );
   }

   @Provides
   @Singleton
   DeviceInfoProvider provideDeviceInfoProvider(Context context) {
      return new DeviceInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   ConnectionInfoProvider connectionInfoProvider(Context context) {
      return new ConnectionInfoProviderImpl(context);
   }

   @Provides
   @Singleton
   AppInfoProvider provideAppInfoProvider(Context context) {
      return new AppInfoProviderImpl(context);
   }

   @Provides
   FileSplitter provideDevice(Context context) {
      return new FileSplitter(context.getExternalCacheDir());
   }

   @Singleton
   @Provides
   FilePathProvider provideFilePathProvider(@ForApplication Context context) {
      return new FilePathProvider(context);
   }

}
