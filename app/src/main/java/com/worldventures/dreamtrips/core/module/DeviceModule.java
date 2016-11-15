package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.jaredrummler.android.device.DeviceName;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.session.model.ImmutableDevice;

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

}
