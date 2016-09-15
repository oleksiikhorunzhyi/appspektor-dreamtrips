package com.worldventures.dreamtrips;

import android.location.Location;

import com.worldventures.dreamtrips.common.RxJavaSchedulerInitializer;
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.techery.janet.ActionService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Location.class, TrackingHelper.class})
public abstract class BaseTest {

   static {
      RxJavaSchedulerInitializer.init();
   }

   @Before
   public void setupBase() {
      MockitoAnnotations.initMocks(this);
      PowerMockito.mock(TrackingHelper.class);
   }

   protected static CacheResultWrapper cachedService(ActionService service) {
      return new CacheResultWrapper(service);
   }
}