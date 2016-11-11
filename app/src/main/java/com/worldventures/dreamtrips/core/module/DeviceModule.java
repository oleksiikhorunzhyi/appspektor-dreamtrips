package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.jaredrummler.android.device.DeviceName;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.session.model.ImmutableDevice;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      library = true, complete = false)
public class DeviceModule {

   @Provides
   @Singleton
   DeviceName.DeviceInfo provideDeviceInfo(@ForApplication Context context) {
      return DeviceName.getDeviceInfo(context);
   }

   @Provides
   @Singleton
   Device provideDevice(DeviceName.DeviceInfo deviceInfo) {
      return ImmutableDevice.builder()
            .manufacturer(deviceInfo.manufacturer)
            .model(deviceInfo.marketName)
            .build();
   }

}
