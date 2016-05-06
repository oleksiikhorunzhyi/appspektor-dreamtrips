package com.worldventures.dreamtrips.modules.picklocation;

import android.app.Activity;

import com.messenger.util.PickLocationDelegate;
import com.worldventures.dreamtrips.modules.picklocation.presenter.PickLocationPresenterImpl;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationResultHandler;
import com.worldventures.dreamtrips.modules.picklocation.util.LocationSettingsDelegate;
import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationActivity;
import com.worldventures.dreamtrips.modules.picklocation.view.PickLocationViewImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                PickLocationPresenterImpl.class,
                PickLocationActivity.class,
                PickLocationViewImpl.class,
        },
        complete = false, library = true
)public class LocationPickerModule {

    @Provides
    @Singleton
    PickLocationDelegate providePickLocationDelegate(Activity activity) {
        return new PickLocationDelegate(activity);
    }

    @Provides
    @Singleton
    LocationSettingsDelegate provideLocationSettingsHandler(Activity activity) {
        return new LocationSettingsDelegate(activity);
    }

    @Provides
    @Singleton
    LocationResultHandler provideLocationResultHandler(Activity activity) {
        return new LocationResultHandler(activity);
    }
}
